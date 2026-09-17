(function () {
  "use strict";

  /* ============================================================
     Atividade A — Algoritmo de Datação e Elevação de Darwin.
     Recebe a altitude (m) e a taxa de soerguimento sísmico
     (cm/século) e calcula a idade mínima para o topo.

     1. Converte a altitude para centímetros;
     2. Proporção: anos = altitudeCm / (cm por ano);
     3. if-else: < 6.000 anos -> alerta (escala dogmática /
        biologicamente inviável); > 1.000.000 anos -> validação
        científica do tempo profundo e da evolução.

     Tenta consumir POST /api/darwin/calculate (Spring Boot);
     sem backend, resolve localmente com a mesma lógica.
     ============================================================ */

  const $ = (sel) => document.querySelector(sel);
  const API = window.KB_API_BASE || "";

  const fmtNum = (n, digits = 0) =>
    Number(n).toLocaleString("pt-BR", {
      minimumFractionDigits: digits,
      maximumFractionDigits: digits
    });

  function fmtYears(years) {
    if (years < 1000) return fmtNum(Math.round(years)) + " anos";
    if (years < 1e6) return fmtNum(years) + " anos";
    if (years < 1e9) return fmtNum(years / 1e6, 2) + " milhões de anos";
    return fmtNum(years / 1e9, 2) + " bilhões de anos";
  }

  /* Espelho do DarwinCalculationService.java (modo local) */
  function calculate(altitudeM, rateCmPerCentury) {
    const altitudeCm = altitudeM * 100;
    const rateCmPerYear = rateCmPerCentury / 100;
    const yearsRequired = altitudeCm / rateCmPerYear;
    const millionsOfYears = yearsRequired / 1e6;

    let classification;
    let kind;
    if (yearsRequired < 6000) {
      classification =
        "ALERTA — escala dogmática e biologicamente inviável: menos de 6.000 anos são insuficientes para a evolução.";
      kind = "alerta";
    } else if (yearsRequired > 1e6) {
      classification =
        "VALIDAÇÃO CIENTÍFICA — tempo profundo: mais de 1 milhão de anos confirmam a evolução e a idade da Terra.";
      kind = "tempo-profundo";
    } else {
      classification = "Escala intermediária — entre 6.000 e 1 milhão de anos.";
      kind = "medio";
    }

    return { altitudeCm, rateCmPerYear, yearsRequired, millionsOfYears, classification, kind };
  }

  const BANNER_STYLES = {
    alerta: "border-red-300 bg-red-50 text-red-700",
    medio: "border-amber-300 bg-amber-50 text-amber-700",
    "tempo-profundo": "border-teal-300 bg-teal-50 text-teal-700"
  };

  function render(result) {
    $("#altitudeCmOut").textContent = fmtNum(result.altitudeCm) + " cm";
    $("#ratePerYearOut").textContent = fmtNum(result.rateCmPerYear, 2) + " cm/ano";
    $("#yearsOut").textContent = fmtYears(result.yearsRequired);
    $("#millionsOut").textContent = result.millionsOfYears < 1
      ? "menos de 1 Ma"
      : fmtNum(result.millionsOfYears, 2) + " milhões de anos";

    const banner = $("#classificationBanner");
    banner.className =
      "mt-4 rounded-2xl border-2 px-4 py-3 text-sm font-bold " +
      BANNER_STYLES[result.kind] +
      (result.kind === "alerta" ? " flex items-start gap-2" : " flex items-start gap-2");
    const icon = result.kind === "alerta" ? "⚠" : result.kind === "tempo-profundo" ? "✓" : "·";
    banner.innerHTML = "<span>" + icon + "</span><span>" + result.classification + "</span>";
  }

  async function onSubmit(e) {
    e.preventDefault();

    const altitudeM = parseFloat($("#altitudeMeters").value);
    const rateCentury = parseFloat($("#rateCentury").value);

    if (!(altitudeM > 0) || !(rateCentury > 0)) {
      $("#loading").classList.add("hidden");
      $("#resultado").classList.add("hidden");
      $("#erro").classList.remove("hidden");
      return;
    }

    $("#erro").classList.add("hidden");
    $("#resultado").classList.remove("hidden");
    $("#loading").classList.remove("hidden");

    let result;
    if (API) {
      try {
        const url =
          API +
          "/api/darwin/calculate?" +
          new URLSearchParams({
            altitudeMeters: altitudeM,
            elevationRatePerCentury: rateCentury
          });
        const res = await fetch(url, { method: "POST" });
        if (!res.ok) throw new Error("HTTP " + res.status);
        const data = await res.json();
        result = {
          altitudeCm: data.altitudeCm,
          rateCmPerYear: data.elevationRatePerCentury / 100,
          yearsRequired: data.yearsRequired,
          millionsOfYears: data.millionsOfYears,
          classification: data.classification,
          kind:
            data.yearsRequired < 6000
              ? "alerta"
              : data.yearsRequired > 1e6
                ? "tempo-profundo"
                : "medio"
        };
      } catch (_err) {
        result = calculate(altitudeM, rateCentury);
      }
    } else {
      result = calculate(altitudeM, rateCentury);
    }

    $("#loading").classList.add("hidden");
    render(result);
  }

  $("#darwinForm").addEventListener("submit", onSubmit);
})();