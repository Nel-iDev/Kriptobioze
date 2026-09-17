(function () {
  "use strict";

  /* ============================================================
     Atividade C — Modelagem Estratigráfica de Fósseis e Camadas.
     Renderiza a tabela do DQL (INNER JOIN + ORDER BY
     profundidade_media DESC). Com o backend ligado, os dados vêm
     de GET /api/fossils/estratigrafia; sem backend, mostra o
     resultado esperado da consulta DQL (database/03_consultas.sql).
     ============================================================ */

  const $ = (sel) => document.querySelector(sel);
  const API = window.KB_API_BASE || "";

  const EXPECTED = [
    { fossilId: 5, nomeCientifico: "Stromatólito de cianobactérias", taxonomia: "Cyanobacteria (microbialito fóssil)", idadeEstimadaMa: 3500, nomeEra: "Arqueano", profundidadeMedia: 10000, tipoRocha: "Metamórfica" },
    { fossilId: 4, nomeCientifico: "Dickinsonia costata", taxonomia: "Proarticulata · Dickinsoniidae", idadeEstimadaMa: 560, nomeEra: "Proterozóico", profundidadeMedia: 5000, tipoRocha: "Metamórfica" },
    { fossilId: 3, nomeCientifico: "Paradoxides sp.", taxonomia: "Trilobita · Paradoxididae", idadeEstimadaMa: 505, nomeEra: "Paleozóico", profundidadeMedia: 1500, tipoRocha: "Sedimentar" },
    { fossilId: 1, nomeCientifico: "Tyrannosaurus rex", taxonomia: "Dinosauria · Theropoda", idadeEstimadaMa: 66, nomeEra: "Mesozóico", profundidadeMedia: 500, tipoRocha: "Sedimentar" },
    { fossilId: 2, nomeCientifico: "Triceratops horridus", taxonomia: "Dinosauria · Ceratopsidae", idadeEstimadaMa: 68, nomeEra: "Mesozóico", profundidadeMedia: 500, tipoRocha: "Sedimentar" },
    { fossilId: 0, nomeCientifico: "Mammuthus primigenius", taxonomia: "Mammalia · Proboscidea", idadeEstimadaMa: 0.04, nomeEra: "Cenozóico", profundidadeMedia: 0, tipoRocha: "Sedimentar" }
  ];

  const fmtNum = (n, digits = 0) =>
    Number(n).toLocaleString("pt-BR", {
      minimumFractionDigits: digits,
      maximumFractionDigits: digits
    });

  const fmtEraAge = (ma) =>
    ma < 1 ? fmtNum(Math.round(ma * 1e6)) + " anos" : fmtNum(ma) + " Ma";

  function render(rows) {
    const body = $("#estratigrafiaBody");
    if (!body) return;

    body.innerHTML = "";
    rows.forEach((f) => {
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
  }

  async function boot() {
    let rows = EXPECTED;

    if (API) {
      try {
        const res = await fetch(API + "/api/fossils/estratigrafia");
        if (res.ok) {
          const data = await res.json();
          if (Array.isArray(data) && data.length) {
            rows = data;
          }
        }
      } catch (_err) {
        /* sem API: mantém o resultado esperado da consulta DQL */
      }
    }

    render(rows);
  }

  boot();
})();