import{I as e,N as t,P as n,a as r,f as i,l as a,o,p as s,t as c,w as l,z as u}from"./jsx-runtime-BYdpQPO7.js";import{t as d}from"./authStore-BPSTVJyI.js";var f=u(e(),1),p=c();function m({children:e}){return(0,p.jsxs)(`html`,{lang:`es`,children:[(0,p.jsxs)(`head`,{children:[(0,p.jsx)(`meta`,{charSet:`utf-8`}),(0,p.jsx)(`meta`,{name:`viewport`,content:`width=device-width, initial-scale=1, shrink-to-fit=no`}),(0,p.jsx)(`link`,{rel:`icon`,type:`image/png`,href:`/assets/images/logo-16x16.png`}),(0,p.jsx)(`link`,{href:`https://fonts.googleapis.com/css?family=Major+Mono+Display`,rel:`stylesheet`}),(0,p.jsx)(`link`,{href:`https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap`,rel:`stylesheet`}),(0,p.jsx)(`link`,{href:`https://cdn.jsdelivr.net/npm/boxicons@2.1.4/css/boxicons.min.css`,rel:`stylesheet`}),(0,p.jsx)(`link`,{href:`/assets/css/bootstrap/bootstrap.min.css`,rel:`stylesheet`}),(0,p.jsx)(`link`,{href:`/assets/css/style.css`,rel:`stylesheet`}),(0,p.jsx)(`link`,{href:`/assets/css/components.css`,rel:`stylesheet`}),(0,p.jsx)(`link`,{href:`/assets/css/auth.css`,rel:`stylesheet`}),(0,p.jsx)(`link`,{href:`/assets/css/forms.css`,rel:`stylesheet`}),(0,p.jsx)(`link`,{href:`/assets/css/profile.css`,rel:`stylesheet`}),(0,p.jsx)(`link`,{href:`/assets/css/media.css`,rel:`stylesheet`}),(0,p.jsx)(`style`,{children:`
          :root {
            --brand: #0b76ff;
            --ink: #0f172a;
            --muted: rgba(15, 23, 42, .65);
            --card: rgba(255, 255, 255, .76);
            --border: rgba(255, 255, 255, .55);
          }
          html, body { height: 100%; }
          body { font-family: Inter, system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; }
          .auth-wrap { position: relative; overflow: hidden; }
          .auth-hero {
            position: relative;
            background: radial-gradient(1200px 700px at 30% 20%, rgba(124, 58, 237, .25), transparent 60%),
                        radial-gradient(1000px 600px at 70% 30%, rgba(14, 165, 233, .22), transparent 55%),
                        linear-gradient(135deg, #050b2a 0%, #060f3c 40%, #050824 100%);
            color: #fff; overflow: hidden; min-height: 100vh;
          }
          .auth-hero::before {
            content: ""; position: absolute; inset: -20%;
            background: radial-gradient(circle at 30% 30%, rgba(255,255,255,.08), transparent 45%),
                        radial-gradient(circle at 80% 60%, rgba(255,255,255,.06), transparent 50%);
            filter: blur(8px); transform: rotate(-10deg); pointer-events: none;
          }
          .hero-lines {
            position: absolute; inset: 0; pointer-events: none;
            background: linear-gradient(110deg, transparent 30%, rgba(167,139,250,.25) 50%, transparent 72%);
            opacity: .7;
            mask-image: radial-gradient(circle at 70% 30%, #000 0 55%, transparent 70%);
            -webkit-mask-image: radial-gradient(circle at 70% 30%, #000 0 55%, transparent 70%);
          }
          .hero-inner {
            position: relative; z-index: 1; display: flex; align-items: center; justify-content: center;
            height: 100%; padding: clamp(24px, 5vw, 64px); text-align: center;
          }
          .hero-title { font-size: clamp(34px, 3.4vw, 56px); font-weight: 800; letter-spacing: -0.02em; margin: 0 0 10px; }
          .hero-sub { max-width: 520px; margin: 0 auto; opacity: .88; }
          .auth-side { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f8fafc; }
          .auth-card {
            width: min(520px, 92%); background: var(--card); backdrop-filter: blur(10px);
            -webkit-backdrop-filter: blur(10px);
            border: 1px solid var(--border); border-radius: 18px;
            box-shadow: 0 18px 55px rgba(0,0,0,.10); padding: 28px 26px;
            animation: pop .55s ease both;
          }
          @keyframes pop {
            from { opacity: 0; transform: translateY(10px) scale(.99); }
            to { opacity: 1; transform: none; }
          }
          .brand-row { display: flex; align-items: center; gap: 12px; margin-bottom: 18px; }
          .brand-mark {
            width: 44px; height: 44px; border-radius: 14px; display: grid; place-items: center;
            background: rgba(11,118,255,.10); border: 1px solid rgba(11,118,255,.18);
            color: var(--brand); font-size: 22px; flex: 0 0 auto;
          }
          .brand-name { font-weight: 800; color: var(--ink); margin: 0; }
          .brand-tag { color: var(--muted); margin: 0; font-size: 13px; }
          .headline { font-size: 22px; font-weight: 800; color: var(--ink); letter-spacing: -0.02em; margin: 10px 0 6px; }
          .subline { color: var(--muted); margin: 0 0 18px; }
          .input-icon { position: relative; }
          .input-icon i { position: absolute; left: 12px; top: 50%; transform: translateY(-50%); font-size: 18px; opacity: .65; pointer-events: none; }
          .input-icon .form-control { padding-left: 40px; height: 44px; border-radius: 12px; }
          .auth-card .btn-primary, .btn-auth { background: var(--brand); border-color: var(--brand); border-radius: 999px; height: 44px; font-weight: 700; letter-spacing: .01em; }
          .auth-card .btn-primary:hover { filter: brightness(.96); }
          .tiny-link { font-size: 13px; }
          @media (max-width: 767px) {
            .auth-hero { min-height: 44vh; }
            .auth-side { min-height: 56vh; }
            .hero-sub { max-width: 320px; }
          }
        `}),(0,p.jsx)(o,{}),(0,p.jsx)(r,{})]}),(0,p.jsxs)(`body`,{className:`newsfeed`,children:[(0,p.jsx)(`div`,{className:`container-fluid`,id:`wrapper`,children:e}),(0,p.jsx)(s,{}),(0,p.jsx)(i,{})]})]})}var h=t(function(){let e=d(e=>e.checkAuth),t=d(e=>e.loading);return(0,f.useEffect)(()=>{e()},[e]),t?(0,p.jsx)(`div`,{className:`d-flex justify-content-center align-items-center`,style:{minHeight:`100vh`},children:(0,p.jsx)(`div`,{className:`spinner-border text-primary`,role:`status`,children:(0,p.jsx)(`span`,{className:`visually-hidden`,children:`Loading...`})})}):(0,p.jsx)(a,{})}),g=n(function({error:e}){let t=`Oops!`,n=`An unexpected error occurred.`;return l(e)&&(t=e.status===404?`404`:`Error`,n=e.status===404?`The requested page could not be found.`:e.statusText||n),(0,p.jsxs)(`main`,{className:`pt-16 p-4 container mx-auto`,children:[(0,p.jsx)(`h1`,{children:t}),(0,p.jsx)(`p`,{children:n}),void 0]})});export{g as ErrorBoundary,m as Layout,h as default};