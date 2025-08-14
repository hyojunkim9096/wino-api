// static/js/admin-session-timer.js
(function(w,d){
  const T = {
    _t:null,       // 1초 타이머
    _poll:null,    // 서버 동기화 타이머
    _opts:null,
    _sec:0,
    _warned:false,

    init(opts){
      this._opts=Object.assign({
        warnMinutes:2,
        defaultMaxSeconds:3600,   // 서버 정보가 없어도 60분에서 시작
        pollMs:15000,             // 주기적 서버 동기화 주기(ms)
        afterLogoutUrl:'/admin/login?logout' // 로그아웃 후 이동
      }, opts||{});

      this._text('--:--');
      this._wireActivity();
      this.refresh().catch(()=>{});

      // (선택) 주기적으로 서버와 남은 시간 보정
      if (this._opts.pollMs > 0) {
        clearInterval(this._poll);
        this._poll = setInterval(() => this._serverSync().catch(()=>{}), this._opts.pollMs);
      }
    },

    // ←★ 응답 해석 공통 함수: remainingSec / remainingSeconds / remaining / expiresAt(ms) 모두 지원
    _parseRemaining(json){
      if (!json || typeof json !== 'object') return this._opts.defaultMaxSeconds;
      if (json.remainingSec != null)      return (json.remainingSec|0);      // 우리 컨트롤러 기본
      if (json.remainingSeconds != null)  return (json.remainingSeconds|0);
      if (json.remaining != null)         return (json.remaining|0);
      if (json.expiresAt != null) {
        // expiresAt: epoch milli
        return Math.max(0, Math.floor((json.expiresAt - Date.now())/1000));
      }
      return this._opts.defaultMaxSeconds;
    },

    async _serverSync(){
      const u=this._opts.infoUrl;
      if(!u) return;
      const res=await fetch(u,{headers:this._csrf(), credentials:'same-origin'});
      if(!res.ok) return;
      const j=await res.json().catch(()=>({}));
      const sec=this._parseRemaining(j);
      if (Number.isFinite(sec)) {
        this._sec = sec;               // 보정만, 타이머는 유지
        this._text(this._fmt(this._sec));
      }
    },

    async refresh(){
      const u=this._opts.infoUrl;
      if(u){
        try{
          const res=await fetch(u,{headers:this._csrf(), credentials:'same-origin'});
          if(res.ok){
            const j=await res.json();
            // ←★ 여기!
            this._sec = this._parseRemaining(j);
          }else{
            this._sec = this._sec || this._opts.defaultMaxSeconds;
          }
        }catch(e){
          this._sec = this._sec || this._opts.defaultMaxSeconds;
        }
      }else{
        this._sec = this._sec || this._opts.defaultMaxSeconds;
      }
      this._start();
    },

    _start(){
      clearInterval(this._t);
      this._warned=false;
      this._t=setInterval(()=>this._tick(),1000);
      this._tick();
    },

    _tick(){
      if(this._sec<=0){ this.logout(); return; }
      this._text(this._fmt(this._sec));
      const warnAt=(this._opts.warnMinutes|0)*60;
      if(this._sec<=warnAt && !this._warned){ this._warned=true; this._openWarn(); }
      this._sec--;
    },

    async extend(){
      const u=this._opts.extendUrl;
      if(!u) return;
      try{
        const res=await fetch(u,{method:'POST', headers:this._csrf(), credentials:'same-origin'});
        if(res.ok){
          const j=await res.json().catch(()=>({}));
          // ←★ 여기!
          this._sec = this._parseRemaining(j);
          this._warned=false;
          this._text(this._fmt(this._sec));
        }
      }catch(e){}
    },

    // ←★ 변경: GET 이동이 아니라 POST + CSRF로 로그아웃 요청 후 리다이렉트
    logout(){
      const url = this._opts.logoutUrl || '/admin/logout';
      // fetch는 302를 따라가도 페이지 네비게이션을 하지 않으므로,
      // 요청만 보내고 수동으로 로그인 화면으로 이동한다.
      fetch(url, { method:'POST', headers:this._csrf(), credentials:'same-origin' })
        .catch(()=>{})
        .finally(()=>{ location.href = this._opts.afterLogoutUrl || '/admin/login?logout'; });
      return false; // onclick에서 기본 동작 막기
    },

    _openWarn(){
      const mEl=this._opts.warnModalEl, bEl=this._opts.stayBtnEl;
      if(!mEl) return;
      try{
        const modal=bootstrap.Modal.getOrCreateInstance(mEl);
        modal.show();
        if(bEl) bEl.onclick=async()=>{ await this.extend(); modal.hide(); };
      }catch(e){}
    },

    _wireActivity(){
      let last=Date.now();
      const act=()=>{ if(Date.now()-last>10000){ this.extend(); } last=Date.now(); };
      ['click','keydown','scroll','mousemove','touchstart'].forEach(ev=>d.addEventListener(ev,act,{passive:true}));
    },

    _text(v){ if(this._opts.displayEl) this._opts.displayEl.textContent=v; },
    _fmt(s){ const m=String(Math.floor(s/60)).padStart(2,'0'); const ss=String(s%60).padStart(2,'0'); return `${m}:${ss}`; },

    _csrf(){
      const h={'Accept':'application/json'};
      // meta 태그 우선 (thymeleaf-layout에 넣은 경우)
      const meta=d.querySelector('meta[name="_csrf"]');
      const header=d.querySelector('meta[name="_csrf_header"]');
      if(meta && header){ h[header.content]=meta.content; return h; }
      // CookieCsrfTokenRepository.withHttpOnlyFalse() 사용 시 쿠키에서 꺼냄
      const v=document.cookie.split('; ').find(x=>x.startsWith('XSRF-TOKEN='));
      if(v) h['X-XSRF-TOKEN']=decodeURIComponent(v.split('=')[1]);
      return h;
    }
  };
  w.AdminSessionTimer=T;
})(window,document);
