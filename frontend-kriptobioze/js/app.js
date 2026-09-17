(function () {
  "use strict";

  /* ============================================================
     Núcleo de integração com o backend (braços de dados)
     O front já conhece os endpoints do Spring Boot. Enquanto o
     backend não estiver no ar, o simulador roda em "modo local".
     Para conectar:  <script>window.KB_API_BASE = "http://localhost:8080";</script>
     (colocar antes do app.js) ou definir no console antes do load.
     ============================================================ */
  const KB = (window.KB = {
    config: {
      apiBase: window.KB_API_BASE || "",
      endpoints: {
        isotopes: "/api/radioactive-decay/isotopes",
        calculate: "/api/radioactive-decay/calculate",
        darwin: "/api/darwin/calculate",
        fossils: "/api/fossils",
        layers: "/api/layers",
        estratigrafia: "/api/fossils/estratigrafia"
      }
    },

    async _request(url, options) {
      const res = await fetch(url, options);
      if (!res.ok) throw new Error("HTTP " + res.status + " - " + url);
      return res.json();
    },

    /* GET /api/radioactive-decay/isotopes */
    getIsotopes() {
      return KB._request(KB.config.apiBase + KB.config.endpoints.isotopes);
    },

    /* POST /api/radioactive-decay/calculate?isotopeSymbol=...&timePeriodYears=... */
    calculateDecay(isotopeSymbol, timePeriodYears) {
      const q = new URLSearchParams({ isotopeSymbol, timePeriodYears });
      return KB._request(
        KB.config.apiBase + KB.config.endpoints.calculate + "?" + q.toString(),
        { method: "POST" }
      );
    },

    /* POST /api/darwin/calculate?altitudeMeters=...&elevationRatePerCentury=... */
    calculateDarwin(altitudeMeters, elevationRatePerCentury) {
      const q = new URLSearchParams({
        altitudeMeters,
        elevationRatePerCentury
      });
      return KB._request(
        KB.config.apiBase + KB.config.endpoints.darwin + "?" + q.toString(),
        { method: "POST" }
      );
    },

    /* GET /api/fossils   GET /api/layers */
    getFossils() {
      return KB._request(KB.config.apiBase + KB.config.endpoints.fossils);
    },
    getLayers() {
      return KB._request(KB.config.apiBase + KB.config.endpoints.layers);
    },

    /* GET /api/fossils/estratigrafia
       INNER JOIN fossil + camada ORDER BY profundidade_media DESC — mesmo DQL
       do script database/03_consultas.sql. A ordem já vem da mais profunda
       (mais antiga) para a superfície (mais recente). */
    getEstratigrafia() {
      return KB._request(KB.config.apiBase + KB.config.endpoints.estratigrafia);
    }
  });

  /* ============================================================
     Dados de isótopos (fallback local = mesmos valores do DataSeeder)
     Os 6 isótopos do PostgreSQL; os 3 primeiros seguem o texto do
     PDF (C-14 até ~50 mil anos; U-235 e K-40 para milhões/bilhões).
     ============================================================ */
  const FALLBACK_ISOTOPES = [
    {
      symbol: "C-14",
      name: "Carbono-14",
      halfLifeYears: 5730,
      range: "até 50 mil anos",
      blurb: "Datação de fósseis e materiais orgânicos recentes"
    },
    {
      symbol: "U-235",
      name: "Urânio-235",
      halfLifeYears: 703800000,
      range: "milhões de anos",
      blurb: "Datação de rochas muito antigas e meteoritos"
    },
    {
      symbol: "K-40",
      name: "Potássio-40",
      halfLifeYears: 1250000000,
      range: "bilhões de anos",
      blurb: "Datação de rochas minerais e fósseis antigos"
    },
    {
      symbol: "U-238",
      name: "Urânio-238",
      halfLifeYears: 4468000000,
      range: "bilhões de anos",
      blurb: "Datação de rochas ígneas e metamórficas"
    },
    {
      symbol: "Rb-87",
      name: "Rubídio-87",
      halfLifeYears: 48800000000,
      range: "bilhões de anos",
      blurb: "Datação de rochas muito antigas e evolução estelar"
    },
    {
      symbol: "Sm-147",
      name: "Samário-147",
      halfLifeYears: 106000000000,
      range: "bilhões de anos",
      blurb: "Datação de meteoritos e formação do sistema solar"
    }
  ];

  /* ---------- estado ---------- */
  let isotopes = FALLBACK_ISOTOPES.slice();
  let apiMode = "local";

  /* Dados de estratigrafia (fallback local = mesmos valores do DataSeeder /
     do arquivo database/02_dados_iniciais.sql). A ordem acompanha o DQL:
     profundidade_media DESC (mais profundo -> mais antigo, primeiro). */
  const FALLBACK_ESTRATIGRAFIA = [
    { fossilId: 5, nomeCientifico: "Stromatólito de cianobactérias", taxonomia: "Cyanobacteria (microbialito fóssil)", idadeEstimadaMa: 3500, nomeEra: "Arqueano", profundidadeMedia: 10000, tipoRocha: "Metamórfica" },
    { fossilId: 4, nomeCientifico: "Dickinsonia costata", taxonomia: "Proarticulata · Dickinsoniidae", idadeEstimadaMa: 560, nomeEra: "Proterozóico", profundidadeMedia: 5000, tipoRocha: "Metamórfica" },
    { fossilId: 3, nomeCientifico: "Paradoxides sp.", taxonomia: "Trilobita · Paradoxididae", idadeEstimadaMa: 505, nomeEra: "Paleozóico", profundidadeMedia: 1500, tipoRocha: "Sedimentar" },
    { fossilId: 1, nomeCientifico: "Tyrannosaurus rex", taxonomia: "Dinosauria · Theropoda", idadeEstimadaMa: 66, nomeEra: "Mesozóico", profundidadeMedia: 500, tipoRocha: "Sedimentar" },
    { fossilId: 2, nomeCientifico: "Triceratops horridus", taxonomia: "Dinosauria · Ceratopsidae", idadeEstimadaMa: 68, nomeEra: "Mesozóico", profundidadeMedia: 500, tipoRocha: "Sedimentar" },
    { fossilId: 0, nomeCientifico: "Mammuthus primigenius", taxonomia: "Mammalia · Proboscidea", idadeEstimadaMa: 0.04, nomeEra: "Cenozóico", profundidadeMedia: 0, tipoRocha: "Sedimentar" }
  ];

  let estratigrafia = FALLBACK_ESTRATIGRAFIA.slice();

  const state = {
    isotopeSymbol: null,
    pct: 50,
    simYears: 0,
    running: false,
    timer: null,
    started: false
  };

  /* ---------- utilidades de formatação (pt-BR) ---------- */
  const $ = (sel) => document.querySelector(sel);
  const fmtNum = (n, digits = 0) =>
    n.toLocaleString("pt-BR", {
      minimumFractionDigits: digits,
      maximumFractionDigits: digits
    });

  function fmtYears(years) {
    if (years < 1000) return fmtNum(Math.round(years)) + " anos";
    if (years < 1e6) return fmtNum(years) + " anos";
    if (years < 1e9) return fmtNum(years / 1e6, 2) + " milhões de anos";
    return fmtNum(years / 1e9, 2) + " bilhões de anos";
  }

  function isotopeOf(symbol) {
    return isotopes.find((i) => i.symbol === symbol) || isotopes[0];
  }

  /* Faixas das eras geológicas (mesmas do DataSeeder, em milhões de anos) */
  const GEOLOGICAL_LAYERS = [
    { from: 0, to: 66e6, name: "Cenozóico", detail: "Era mais recente" },
    { from: 66e6, to: 252e6, name: "Mesozóico", detail: "Era dos dinossauros" },
    { from: 252e6, to: 541e6, name: "Paleozóico", detail: "Era da vida antiga" },
    { from: 541e6, to: 2500e6, name: "Proterozóico", detail: "Era da vida primitiva" },
    { from: 2500e6, to: 4000e6, name: "Arqueano", detail: "Era mais antiga" }
  ];

  function eraOf(years) {
    for (const layer of GEOLOGICAL_LAYERS) {
      if (years >= layer.from && years < layer.to) return layer;
    }
    if (years >= 4000e6) {
      return { from: 4000e6, to: Infinity, name: "Pré-Arqueano", detail: "Antes da crosta terrestre registrada no banco" };
    }
    return GEOLOGICAL_LAYERS[0];
  }

  function calcYearsFromPct(pct, halfLife) {
    return halfLife * Math.log2(100 / pct);
  }

  function calcPctFromYears(years, halfLife) {
    return 100 * Math.pow(0.5, years / halfLife);
  }

  /* ============================================================
     Interface: isótopos (mini cards estilo tabela periódica)
     ============================================================ */
  function renderIsotopeGroup() {
    const group = $("#isotopeGroup");
    group.innerHTML = "";

    isotopes.forEach((iso) => {
      const parts = iso.symbol.split("-");
      const letter = parts[0] || iso.symbol;
      const mass = parts[1] || "";

      const btn = document.createElement("button");
      btn.type = "button";
      btn.className = "isotope-tile group relative aspect-square";
      btn.dataset.symbol = iso.symbol;
      btn.setAttribute("role", "radio");
      btn.setAttribute("aria-checked", "false");
      btn.setAttribute("aria-label", iso.name);
      btn.title = iso.name + " · Meia-vida ~" + fmtYears(iso.halfLifeYears);

      btn.innerHTML =
        '<span class="iso-num"></span>' +
        '<span class="iso-sym absolute inset-0 flex items-center justify-center pb-0.5"></span>';
      btn.querySelector(".iso-num").textContent = mass;
      btn.querySelector(".iso-sym").textContent = letter;

      btn.addEventListener("click", () => {
        state.isotopeSymbol = iso.symbol;
        state.simYears = calcYearsFromPct(state.pct, iso.halfLifeYears);
        syncIsotopeCards();
        renderAll();
      });

      group.appendChild(btn);
    });

    syncIsotopeCards();
  }

  function syncIsotopeCards() {
    document.querySelectorAll("#isotopeGroup .isotope-tile").forEach((card) => {
      const on = card.dataset.symbol === state.isotopeSymbol;
      card.classList.toggle("iso-on", on);
      card.setAttribute("aria-checked", on ? "true" : "false");
    });
    updateInfoCard();
    updateGuideVisibility();
  }

  function updateInfoCard() {
    const el = $("#isotopeInfoText");
    if (!el) return;
    if (!state.isotopeSymbol) {
      el.innerHTML =
        '<span class="text-sm font-semibold italic text-stone-400">Selecione um dos isótopos</span>';
      return;
    }
    const iso = isotopeOf(state.isotopeSymbol);
    el.innerHTML =
      '<span class="font-display text-base font-extrabold text-kripta-navy">' + iso.name + "</span>" +
      '<span class="ml-2 text-sm font-semibold text-stone-500">Meia-vida ~' + fmtYears(iso.halfLifeYears) + "</span>";
  }

  function updateGuideVisibility() {
    const on = !!state.isotopeSymbol;
    const wrap2 = $("#step2Wrap");
    const wrap3 = $("#step3Wrap");
    const curve = $("#stepCurveWrap");
    const leituras = $("#stepLeiturasWrap");
    if (wrap2) wrap2.classList.toggle("kb-muted", !on);
    if (wrap3) wrap3.classList.toggle("kb-muted", !on);
    if (curve) curve.classList.toggle("kb-muted", !on);
    if (leituras) leituras.classList.toggle("kb-muted", !on);
  }

  /* ---------- grid de átomos (9x9 em todas as telas) ---------- */
  const ATOMS_FULL = 81;
  const ATOMS_MOBILE = 81;
  const isMobileView = () => window.matchMedia("(max-width: 639px)").matches;

  function atomCount() {
    return isMobileView() ? ATOMS_MOBILE : ATOMS_FULL;
  }

  function atomSeed(i) {
    const x = Math.sin(i * 12.9898 + 4.1414) * 43758.5453;
    return x - Math.floor(x);
  }

  function renderAtoms() {
    const grid = $("#atomGrid");
    const total = atomCount();
    if (grid.childElementCount !== total) {
      grid.innerHTML = "";
      for (let i = 0; i < total; i++) {
        const cell = document.createElement("div");
        cell.className = "atom";
        cell.setAttribute("aria-hidden", "true");
        grid.appendChild(cell);
      }
    }
    const threshold = state.pct / 100;
    grid.querySelectorAll(".atom").forEach((cell, i) => {
      const alive = atomSeed(i) <= threshold;
      cell.classList.toggle("active", alive);
      cell.classList.toggle("decayed", !alive);
    });
  }

  /* ---------- curva de decaimento exponencial (SVG) ---------- */
  const MAX_HALF_LIVES = 6;

  function drawCurve(viewH, guidesEl, pointsEl, markerEl) {
    const iso = isotopeOf(state.isotopeSymbol);
    const halfLife = iso.halfLifeYears;
    const maxYears = halfLife * MAX_HALF_LIVES;
    const years = Math.min(maxYears, Math.max(0, state.simYears));

    const points = [];
    for (let i = 0; i <= 120; i++) {
      const t = (i / 120) * maxYears;
      const rem = Math.pow(0.5, t / halfLife);
      points.push((i / 120) * 100 + "," + (5 + (1 - rem) * (viewH - 10)).toFixed(2));
    }

    const remNow = Math.pow(0.5, years / halfLife);
    const curX = (years / maxYears) * 100;
    const curY = 5 + (1 - remNow) * (viewH - 10);

    pointsEl.setAttribute("points", points.join(" "));
    markerEl.setAttribute("cx", curX.toFixed(2));
    markerEl.setAttribute("cy", curY.toFixed(2));

    // linhas-guia horizontais: 50% e 25% da massa restante
    const g50 = 5 + (1 - 0.5) * (viewH - 10);
    const g25 = 5 + (1 - 0.25) * (viewH - 10);
    guidesEl.innerHTML =
      '<line x1="0" y1="' + g50 + '" x2="100" y2="' + g50 + '" stroke="#f59e0b" stroke-width="0.35" stroke-dasharray="3 3" opacity="0.55"/>' +
      '<text x="100" y="' + (g50 - 2) + '" text-anchor="end" font-size="3" fill="#b45309" font-family="inherit">50%</text>' +
      '<line x1="0" y1="' + g25 + '" x2="100" y2="' + g25 + '" stroke="#f59e0b" stroke-width="0.35" stroke-dasharray="3 3" opacity="0.55"/>' +
      '<text x="100" y="' + (g25 - 2) + '" text-anchor="end" font-size="3" fill="#b45309" font-family="inherit">25%</text>';
  }

  function buildCurve() {
    // curva única dentro da visualização (viewBox 100x52)
    drawCurve(52, $("#curveGuides"), $("#curvePoints"), $("#curveMarker"));
  }

  /* ---------- leituras + barra de massa ---------- */
  function renderReadouts() {
    if (!state.isotopeSymbol) {
      const age = $("#ageOut");
      if (age) age.textContent = "—";
      const era = $("#eraOut");
      if (era) {
        era.textContent = "—";
        era.title = "";
      }
      const hl = $("#hlOut");
      if (hl) hl.textContent = "—";
      const time = $("#timeOut");
      if (time) time.textContent = "—";
      const pct = $("#pctOut");
      if (pct) pct.textContent = "—";
      const bar = $("#massBar");
      if (bar) bar.style.width = "0%";
      const name = $("#isotopeName");
      if (name) name.textContent = "Nenhum isótopo selecionado";
      const label = $("#isotopeBarLabel");
      if (label) label.textContent = "—";
      return;
    }

    const iso = isotopeOf(state.isotopeSymbol);
    const halfLife = iso.halfLifeYears;
    const halfLives = state.pct < 100 ? Math.log2(100 / state.pct) : 0;

    $("#ageOut").textContent = fmtYears(state.simYears || calcYearsFromPct(state.pct, halfLife));
    const era = eraOf(state.simYears || calcYearsFromPct(state.pct, halfLife));
    const eraEl = $("#eraOut");
    if (eraEl) {
      eraEl.textContent = era.name;
      eraEl.title = era.detail + " (" + fmtYears(era.from) + " — " + (era.to === Infinity ? "hoje" : fmtYears(era.to)) + ")";
    }
    $("#hlOut").textContent = fmtNum(halfLives, 2).replace(".", ",") + " meias-vidas";
    $("#timeOut").textContent = fmtYears(state.simYears);
    $("#pctOut").textContent = fmtNum(state.pct, 1).replace(".", ",") + "% do isótopo na rocha";
    $("#massBar").style.width = state.pct.toFixed(1) + "%";

    const isoName = $("#isotopeName");
    if (isoName) isoName.textContent = iso.name + " (" + iso.symbol + ")";
  }

  function renderAll() {
    renderAtoms();
    buildCurve();
    renderReadouts();
  }

  /* ---------- tabela de estratigrafia (DQL: INNER JOIN + ORDER BY profundidade DESC) ---------- */
  function fmtEraAge(ma) {
    return ma < 1
      ? fmtNum(Math.round(ma * 1e6)) + " anos"
      : fmtNum(ma) + " Ma";
  }

  function renderEstratigrafia() {
    const body = $("#estratigrafiaBody");
    const note = $("#estratigrafiaNote");
    if (!body) return;

    body.innerHTML = "";
    estratigrafia.forEach((f) => {
      const tr = document.createElement("tr");
      tr.className = "border-b border-orange-100 last:border-0 hover:bg-amber-50";
      tr.innerHTML =
        '<td class="px-4 py-3"><span class="inline-flex min-w-[3.5rem] items-center justify-center rounded-lg bg-kripta-orange px-2 py-1 text-xs font-bold text-white">' +
        fmtNum(f.profundidadeMedia) + " m</span></td>" +
        '<td class="px-4 py-3 font-bold text-kripta-navy">' + f.nomeEra + "</td>" +
        '<td class="px-4 py-3 text-stone-600">' + f.tipoRocha + "</td>" +
        '<td class="px-4 py-3 font-bold italic text-stone-800">' + f.nomeCientifico + "</td>" +
        '<td class="px-4 py-3 text-stone-600">' + f.taxonomia + "</td>" +
        '<td class="px-4 py-3 font-bold text-stone-800">' + fmtEraAge(f.idadeEstimadaMa) + "</td>";
      body.appendChild(tr);
    });

    if (note) {
      note.textContent = apiMode === "api"
        ? "Consultado via GET /api/fossils/estratigrafia — mesma consulta DQL executada no PostgreSQL."
        : "Modo local — mostra o resultado esperado da consulta DQL (database/03_consultas.sql), com o backend ligado a tabela vem da API.";
    }
  }

  /* ---------- controles: simular / resetar ---------- */
  function startSim() {
    if (state.running) return;
    if (!state.isotopeSymbol) return; // sem isótopo selecionado, não simula
    state.running = true;
    state.started = true;
    $("#btnPlay").textContent = "⏸";
    $("#btnPlay").classList.remove("bg-kripta-navy");
    $("#btnPlay").classList.add("bg-pink-600");
    state.timer = setInterval(tick, 60);
  }

  function stopSim() {
    state.running = false;
    clearInterval(state.timer);
    state.timer = null;
    $("#btnPlay").textContent = "▶";
    $("#btnPlay").classList.add("bg-kripta-navy");
    $("#btnPlay").classList.remove("bg-pink-600");
  }

  function tick() {
    const iso = isotopeOf(state.isotopeSymbol);
    state.simYears += iso.halfLifeYears * 0.02; // percorre ~6 meias-vidas em ~18s
    state.pct = calcPctFromYears(state.simYears, iso.halfLifeYears);
    if (state.pct < 0.05 || state.simYears >= iso.halfLifeYears * MAX_HALF_LIVES) {
      state.pct = Math.max(0.05, state.pct);
      stopSim();
    }
    $("#rangePct").value = state.pct;
    renderAll();
  }

  function resetSim() {
    stopSim();
    state.pct = 100;
    state.simYears = 0;
    state.started = false;
    $("#rangePct").value = 100;
    renderAll();
  }

  /* ============================================================
     Drawer lateral (menu hambúrguer)
     ============================================================ */
  const drawer = $("#drawer");
  const backdrop = $("#backdrop");
  const btnMenu = $("#btnMenu");

  function openDrawer() {
    drawer.classList.remove("translate-x-full");
    backdrop.classList.remove("hidden");
    btnMenu.setAttribute("aria-expanded", "true");
    document.body.classList.add("overflow-hidden");
  }

  function closeDrawer() {
    drawer.classList.add("translate-x-full");
    backdrop.classList.add("hidden");
    btnMenu.setAttribute("aria-expanded", "false");
    document.body.classList.remove("overflow-hidden");
  }

  btnMenu.addEventListener("click", openDrawer);
  $("#btnCloseDrawer").addEventListener("click", closeDrawer);
  backdrop.addEventListener("click", closeDrawer);
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") closeDrawer();
  });
  drawer.querySelectorAll("a[href^='#']").forEach((a) =>
    a.addEventListener("click", closeDrawer)
  );

  /* ---------- painel ---------- */
  $("#rangePct").addEventListener("input", (e) => {
    state.pct = parseFloat(e.target.value);
    const iso = isotopeOf(state.isotopeSymbol);
    state.simYears = calcYearsFromPct(state.pct, iso.halfLifeYears);
    if (state.running) stopSim();
    renderAll();
  });

  $("#btnPlay").addEventListener("click", () =>
    state.running ? stopSim() : startSim()
  );
  $("#btnReset").addEventListener("click", resetSim);

  /* ---------- controles flutuantes: torre play/reset fixa à direita ---------- */
  const NAV_OFFSET = 80; // 64px do menu fixo + folga

  function placeFloatingControls() {
    const tower = $("#controlsTower");
    const card1 = $("#step1Card");
    if (!tower || !card1) return;

    const vh = window.innerHeight;
    const card1Top = card1.getBoundingClientRect().top;

    // hero ainda dominando a tela: torre fica escondida
    if (card1Top >= vh) {
      tower.classList.add("kb-controls--off");
      return;
    }
    tower.classList.remove("kb-controls--off");

    const footer = document.querySelector("footer");
    const footerTop = footer ? footer.getBoundingClientRect().top : Infinity;
    const towerH = tower.offsetHeight;

    // assim que o topo do rodapé entra na tela, a torre "para" pouco acima dele
    if (footerTop - FOOTER_BREAK <= vh) {
      tower.style.top = Math.max(NAV_OFFSET, footerTop - towerH - FOOTER_BREAK) + "px";
    } else {
      // alinhada ao card 1 enquanto visível; depois segue fixa abaixo do menu
      tower.style.top = Math.max(NAV_OFFSET, card1Top) + "px";
    }
  }

  window.addEventListener("scroll", placeFloatingControls, { passive: true });

  window.addEventListener("resize", () => {
    renderAtoms();
    placeFloatingControls();
  });

  /* ============================================================
     Boot: tenta carregar os isótopos do backend (fallback local)
     ============================================================ */
  async function boot() {
    renderIsotopeGroup();
    renderEstratigrafia();
    renderAll();

    try {
      const list = await KB.getIsotopes();
      const mapped = list
        .map((it) => ({
          symbol: it.symbol,
          name: it.name,
          halfLifeYears: it.halfLifeYears,
          range: it.halfLifeYears < 1e6 ? "até 50 mil anos" : it.halfLifeYears < 1e9 ? "milhões de anos" : "bilhões de anos",
          blurb: it.usedFor || it.description || ""
        }));

      if (mapped.length) {
        isotopes = mapped;
        apiMode = "api";
        renderIsotopeGroup();
        renderAll();
      }
    } catch (_err) {
      apiMode = "local";
    }

    try {
      const rows = await KB.getEstratigrafia();
      if (Array.isArray(rows) && rows.length) {
        estratigrafia = rows;
        apiMode = "api";
        renderEstratigrafia();
      }
    } catch (_err) {
      /* sem API: mantém o fallback local */
    }

    renderEstratigrafia();
    placeFloatingControls();
  }

  boot();
})();