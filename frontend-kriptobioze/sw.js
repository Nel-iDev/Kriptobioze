/* Service Worker do Kriptobioze (PWA) */
const VERSION = "kb-pwa-v1.0";
const PRECACHE = [
  "index.html",
  "css/styles.css",
  "js/app.js",
  "manifest.json",
  "icons/icon-192.png",
  "icons/icon-512.png"
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(VERSION).then((cache) => cache.addAll(PRECACHE)).then(() => self.skipWaiting())
  );
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches
      .keys()
      .then((keys) => Promise.all(keys.filter((k) => k !== VERSION).map((k) => caches.delete(k))))
      .then(() => self.clients.claim())
  );
});

function toCacheKey(url) {
  const base = self.registration.scope;
  let path = url.href;
  if (path.startsWith(base)) path = path.slice(base.length);
  else path = url.pathname.split("/").pop() || "index.html";
  return decodeURIComponent(path.split("?")[0]);
}

self.addEventListener("fetch", (event) => {
  const req = event.request;
  if (req.method !== "GET") return;
  const url = new URL(req.url);
  if (url.origin !== self.location.origin) return;

  event.respondWith(
    (async () => {
      const key = toCacheKey(url);

      if (req.mode === "navigate") {
        const hit = await caches.match("index.html");
        const page = (await caches.match(key)) || hit;
        if (page) return page;
        const fresh = await fetch(req);
        const cache = await caches.open(VERSION);
        cache.put("index.html", fresh.clone());
        return fresh;
      }

      const cached = await caches.match(key);
      if (cached) return cached;

      try {
        const fresh = await fetch(req);
        const cache = await caches.open(VERSION);
        cache.put(key, fresh.clone());
        return fresh;
      } catch (err) {
        const index = await caches.match("index.html");
        if (index) return index;
        throw err;
      }
    })()
  );
});