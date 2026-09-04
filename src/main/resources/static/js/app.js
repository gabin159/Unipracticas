// Carga fragmentos HTML dinámicamente (header, footer, sidebars)
async function loadFragment(url, elementId) {
  try {
    const res = await fetch(url);
    if (res.ok) {
      document.getElementById(elementId).innerHTML = await res.text();
    }
  } catch (err) {
    console.error("Error cargando el fragmento:", err);
  }
}

// Plantilla HTML para renderizar cada tarjeta de práctica
function createPracticeCardHTML(p) {
  return `
    <div class="bg-white rounded-xl border border-slate-200 shadow-sm p-5 hover:shadow-md hover:border-blue-200 transition-all cursor-pointer" onclick="window.location.href='empresa-detalle.html?id=${p.id}'">
      <div class="flex items-start gap-3 mb-3">
        <div class="w-10 h-10 rounded-lg ${p.color} flex items-center justify-center flex-shrink-0">
          <span class="text-white text-xs font-bold">${p.initials}</span>
        </div>
        <div class="min-w-0">
          <h3 class="font-semibold text-slate-900 text-sm leading-snug">${p.title}</h3>
          <p class="text-slate-500 text-xs mt-0.5">${p.company}</p>
        </div>
      </div>
      <div class="flex flex-wrap gap-1.5 mb-3">
        <span class="text-xs font-medium px-2 py-0.5 rounded-full border bg-blue-50 text-blue-700 border-blue-200">${p.area}</span>
        <span class="text-xs font-medium px-2 py-0.5 rounded-full border bg-slate-100 text-slate-600 border-slate-200">${p.modalidad}</span>
        <span class="text-xs font-medium px-2 py-0.5 rounded-full border bg-slate-100 text-slate-600 border-slate-200">${p.duration}</span>
      </div>
      <div class="flex items-center justify-between text-xs text-slate-500">
        <span class="flex items-center gap-1">📍 ${p.location}</span>
        <span class="font-medium text-slate-700">${p.vacantes} vacante${p.vacantes !== 1 ? "s" : ""}</span>
      </div>
    </div>
  `;
}

// Renderiza las 3 mejores prácticas en el home
function renderFeaturedPractices() {
  const container = document.getElementById("featured-practices");
  if (!container || typeof practices === "undefined") return;
  container.innerHTML = practices.slice(0, 3).map(createPracticeCardHTML).join("");
}

// Lógica de filtrado dinámico para la lista de prácticas (empresas.html)
function initPracticesList() {
  const grid = document.getElementById("practices-grid");
  const inputSearch = document.getElementById("input-search");
  const selectArea = document.getElementById("select-area");
  const selectModalidad = document.getElementById("select-modalidad");
  const counter = document.getElementById("results-counter");

  if (!grid || typeof practices === "undefined") return;

  // Llenar select de áreas dinámicamente
  const areas = ["Todas", ...new Set(practices.map(p => p.area))];
  selectArea.innerHTML = areas.map(a => `<option value="${a}">${a}</option>`).join("");

  function filter() {
    const searchVal = inputSearch.value.toLowerCase();
    const areaVal = selectArea.value;
    const modalidadVal = selectModalidad.value;

    const filtered = practices.filter(p => {
      const matchSearch = searchVal === "" || p.title.toLowerCase().includes(searchVal) || p.company.toLowerCase().includes(searchVal);
      const matchArea = areaVal === "Todas" || p.area === areaVal;
      const matchModalidad = modalidadVal === "Todas" || p.modalidad === modalidadVal;
      return matchSearch && matchArea && matchModalidad;
    });

    counter.textContent = `${practices.length} prácticas publicadas · ${filtered.length} resultados`;

    if (filtered.length > 0) {
      grid.innerHTML = filtered.map(createPracticeCardHTML).join("");
    } else {
      grid.innerHTML = `
        <div class="col-span-3 text-center py-16 text-slate-400">
          <p class="text-3xl mb-3">🔍</p>
          <p class="font-medium text-slate-600">Sin resultados</p>
          <p class="text-sm mt-1">Intenta con otros filtros</p>
        </div>
      `;
    }
  }

  inputSearch.addEventListener("input", filter);
  selectArea.addEventListener("change", filter);
  selectModalidad.addEventListener("change", filter);

  filter();
}