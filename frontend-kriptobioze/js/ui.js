(function () {
  "use strict";

  /* ============================================================
     UI compartilhada das páginas do Kriptobioze (menu lateral +
     registro do service worker). Usado por backA.html e dataC.html.
     ============================================================ */

  function initDrawer() {
    const drawer = document.getElementById("drawer");
    const backdrop = document.getElementById("backdrop");
    const openBtn = document.getElementById("btnMenu");
    const closeBtn = document.getElementById("btnCloseDrawer");

    if (!drawer || !backdrop || !openBtn) return;

    const open = () => {
      drawer.classList.remove("translate-x-full");
      backdrop.classList.remove("hidden");
      drawer.setAttribute("aria-hidden", "false");
      openBtn.setAttribute("aria-expanded", "true");
      document.body.style.overflow = "hidden";
    };

    const close = () => {
      drawer.classList.add("translate-x-full");
      backdrop.classList.add("hidden");
      drawer.setAttribute("aria-hidden", "true");
      openBtn.setAttribute("aria-expanded", "false");
      document.body.style.overflow = "";
    };

    openBtn.addEventListener("click", open);
    if (closeBtn) closeBtn.addEventListener("click", close);
    backdrop.addEventListener("click", close);
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") close();
    });
    drawer
      .querySelectorAll("a[href^='#'], a[href$='.html']")
      .forEach((a) => a.addEventListener("click", close));
  }

  function initSW() {
    if (
      "serviceWorker" in navigator &&
      (location.protocol === "https:" ||
        location.hostname === "localhost" ||
        location.hostname === "127.0.0.1")
    ) {
      window.addEventListener("load", function () {
        navigator.serviceWorker.register("../sw.js").catch(function (err) {
          console.warn("Service Worker: registro falhou", err);
        });
      });
    }
  }

  document.addEventListener("DOMContentLoaded", () => {
    initDrawer();
    initSW();
  });
})();