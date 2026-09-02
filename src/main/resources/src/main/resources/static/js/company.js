/* ============================================================
   company.js
   Interactividad del panel de EMPRESA (vanilla JS).

   Esto reemplaza el useState de React: en vez de guardar el
   estado en memoria de un componente, lo guardamos en el propio
   DOM (clases, atributos data-*) y lo recalculamos cuando algo
   cambia. Cuando conectes el backend, cada bloque marcado con
   "TODO backend" es donde deberías reemplazar la simulación por
   un fetch() real a tu controller de Spring.
   ============================================================ */

document.addEventListener("DOMContentLoaded", () => {
  initPerfilEmpresa();
  initPostulantes();
  initEvaluacion();
  initMisPracticas();
});


/* ============================================================
   1. PERFIL DE EMPRESA (perfil.html)
   ============================================================ */
function initPerfilEmpresa() {
  const btnEditar = document.getElementById("btnEditarDatos");
  if (btnEditar) {
    btnEditar.addEventListener("click", () => {
      const campos = document.querySelectorAll("#camposEmpresa .campo-editable");
      const editando = btnEditar.dataset.editando === "true";

      campos.forEach((input) => {
        input.disabled = editando; // si estaba editando, ahora se bloquea
        input.classList.toggle("border", !editando);
        input.classList.toggle("border-slate-200", !editando);
        input.classList.toggle("rounded-lg", !editando);
        input.classList.toggle("px-2", !editando);
        input.classList.toggle("py-1", !editando);
      });

      if (editando) {
        // Estábamos editando -> ahora se guarda
        btnEditar.textContent = "✏️ Editar";
        btnEditar.classList.remove("bg-emerald-600");
        btnEditar.classList.add("bg-blue-700");
        btnEditar.dataset.editando = "false";

        // TODO backend: enviar el formulario #datosForm con fetch/POST
        // fetch('/company/perfil', { method: 'POST', body: new FormData(document.getElementById('datosForm')) })
      } else {
        btnEditar.textContent = "✓ Guardar";
        btnEditar.classList.remove("bg-blue-700");
        btnEditar.classList.add("bg-emerald-600");
        btnEditar.dataset.editando = "true";
      }
    });
  }

  // Subida del documento pendiente (Formato de vinculación)
  const inputFormato = document.getElementById("inputFormatoVinculacion");
  if (inputFormato) {
    inputFormato.addEventListener("change", () => {
      if (!inputFormato.files.length) return;
      const fila = document.getElementById("docFormatoVinculacion");
      fila.querySelector(".doc-badge").textContent = "Cargado";
      fila.querySelector(".doc-badge").className =
        "text-xs font-medium px-2 py-0.5 rounded-full border bg-emerald-50 text-emerald-700 border-emerald-200 doc-badge";
      fila.querySelector(".doc-fecha").textContent = "Cargado hoy";
      fila.querySelector(".doc-accion").outerHTML =
        '<button type="button" class="text-xs text-slate-500 border border-slate-200 px-3 py-1.5 rounded-lg hover:bg-slate-100">Ver</button>';

      actualizarContadorDocumentos();

      // TODO backend: subir el archivo real
      // const fd = new FormData(); fd.append('archivo', inputFormato.files[0]);
      // fetch('/company/documentos/formato-vinculacion', { method: 'POST', body: fd })
    });
  }

  // Logo de la empresa
  const btnEliminarLogo = document.getElementById("btnEliminarLogo");
  const inputLogo = document.getElementById("inputLogo");
  if (btnEliminarLogo) {
    btnEliminarLogo.addEventListener("click", () => {
      document.getElementById("logoConLogo").classList.add("hidden");
      document.getElementById("logoSinLogo").classList.remove("hidden");
      // TODO backend: DELETE /company/logo
    });
  }
  if (inputLogo) {
    inputLogo.addEventListener("change", () => {
      if (!inputLogo.files.length) return;
      document.getElementById("logoSinLogo").classList.add("hidden");
      document.getElementById("logoConLogo").classList.remove("hidden");
      // TODO backend: subir el logo nuevo
    });
  }
}

function actualizarContadorDocumentos() {
  const filas = document.querySelectorAll("#listaDocumentos .doc-row");
  const cargados = document.querySelectorAll("#listaDocumentos .doc-badge:not(.bg-amber-50), #listaDocumentos [data-status='vigente']");
  const contador = document.getElementById("contadorDocs");
  if (contador) contador.textContent = `${filas.length} / ${filas.length} cargados`;
}


/* ============================================================
   2. POSTULANTES (postulantes.html)
   ============================================================ */
function initPostulantes() {
  const tabla = document.getElementById("tablaPostulantes");
  if (!tabla) return;

  const panelDetalle = document.getElementById("panelDetalle");
  const layout = document.getElementById("layoutPostulantes");

  // Clic en una fila -> abre/cierra el panel de detalle
  tabla.querySelectorAll(".applicant-row").forEach((fila) => {
    fila.addEventListener("click", (e) => {
      // Si el clic fue sobre un botón o link dentro de la fila, no abrir el panel
      if (e.target.closest("button, a")) return;

      const yaSeleccionada = fila.classList.contains("is-selected");
      tabla.querySelectorAll(".applicant-row").forEach((f) => f.classList.remove("is-selected"));

      if (yaSeleccionada) {
        fila.classList.remove("is-selected");
        panelDetalle.classList.add("hidden");
        layout.style.gridTemplateColumns = "1fr";
      } else {
        fila.classList.add("is-selected");
        renderDetalle(fila);
        panelDetalle.classList.remove("hidden");
        layout.style.gridTemplateColumns = "2fr 1fr";
      }
    });

    // Botones aceptar / rechazar / deshacer
    fila.querySelector(".btn-aceptar")?.addEventListener("click", (e) => {
      e.stopPropagation();
      cambiarEstado(fila, "Aceptado");
    });
    fila.querySelector(".btn-rechazar")?.addEventListener("click", (e) => {
      e.stopPropagation();
      cambiarEstado(fila, "Rechazado");
    });
    fila.querySelector(".btn-deshacer")?.addEventListener("click", (e) => {
      e.stopPropagation();
      cambiarEstado(fila, "Pendiente");
    });
  });

  actualizarContadores();
}

function cambiarEstado(fila, nuevoEstado) {
  fila.dataset.estado = nuevoEstado;

  const badge = fila.querySelector(".estado-badge");
  const estilos = {
    Pendiente: "bg-amber-50 text-amber-700 border-amber-200",
    Aceptado: "bg-emerald-50 text-emerald-700 border-emerald-200",
    Rechazado: "bg-red-50 text-red-700 border-red-200",
  };
  badge.textContent = nuevoEstado;
  badge.className = `estado-badge text-xs font-medium px-2 py-0.5 rounded-full border ${estilos[nuevoEstado]}`;

  const acciones = fila.querySelector(".acciones-pendiente");
  const deshacer = fila.querySelector(".btn-deshacer");
  if (nuevoEstado === "Pendiente") {
    acciones.classList.remove("hidden");
    deshacer.classList.add("hidden");
  } else {
    acciones.classList.add("hidden");
    deshacer.classList.remove("hidden");
  }

  actualizarContadores();

  // Si el panel de detalle está mostrando a este mismo estudiante, lo refrescamos
  if (fila.classList.contains("is-selected")) renderDetalle(fila);

  // TODO backend: notificar el cambio de estado al servidor (y este, al tutor)
  // fetch(`/company/postulantes/${fila.dataset.id}/estado`, { method:'POST', body: new URLSearchParams({estado: nuevoEstado}) })
}

function actualizarContadores() {
  const filas = document.querySelectorAll("#tablaPostulantes .applicant-row");
  const total = filas.length;
  const contar = (estado) => [...filas].filter((f) => f.dataset.estado === estado).length;

  document.getElementById("totalPostulantes").textContent = total;
  document.getElementById("countPendiente").textContent = contar("Pendiente");
  document.getElementById("countAceptado").textContent = contar("Aceptado");
  document.getElementById("countRechazado").textContent = contar("Rechazado");
}

function renderDetalle(fila) {
  const panel = document.getElementById("panelDetalle");
  const d = fila.dataset;
  const estilos = {
    Pendiente: "bg-amber-50 text-amber-700 border-amber-200",
    Aceptado: "bg-emerald-50 text-emerald-700 border-emerald-200",
    Rechazado: "bg-red-50 text-red-700 border-red-200",
  };

  let botones = "";
  if (d.estado === "Pendiente") {
    botones = `
      <div class="flex gap-2">
        <button type="button" id="detalleAceptar" class="flex-1 bg-emerald-600 text-white text-sm font-bold py-2.5 rounded-xl hover:bg-emerald-700">✓ Aceptar estudiante</button>
        <button type="button" id="detalleRechazar" class="flex-1 bg-red-500 text-white text-sm font-bold py-2.5 rounded-xl hover:bg-red-600">✗ Rechazar</button>
      </div>`;
  } else if (d.estado === "Aceptado") {
    botones = `
      <div class="bg-emerald-50 border border-emerald-200 rounded-xl p-3 text-center">
        <p class="text-emerald-800 text-sm font-semibold">✓ Estudiante aceptado</p>
        <p class="text-emerald-600 text-xs mt-0.5">El tutor será notificado automáticamente</p>
      </div>`;
  } else {
    botones = `
      <div class="bg-red-50 border border-red-200 rounded-xl p-3 text-center">
        <p class="text-red-800 text-sm font-semibold">✗ Estudiante rechazado</p>
        <p class="text-red-600 text-xs mt-0.5">El tutor será notificado automáticamente</p>
      </div>`;
  }

  panel.innerHTML = `
    <div class="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
      <div class="flex items-start gap-3 mb-4">
        <div class="w-12 h-12 rounded-full bg-blue-600 flex items-center justify-center text-white font-bold text-lg flex-shrink-0">
          ${d.nombre.charAt(0)}
        </div>
        <div class="flex-1">
          <h3 class="font-bold text-slate-900">${d.nombre}</h3>
          <p class="text-slate-500 text-sm">${d.programa} · ${d.semestre} semestre</p>
          <span class="estado-badge text-xs font-medium px-2 py-0.5 rounded-full border mt-1 inline-block ${estilos[d.estado]}">${d.estado}</span>
        </div>
        <button type="button" id="detalleCerrar" class="text-slate-300 hover:text-slate-600 text-lg">×</button>
      </div>

      <div class="grid grid-cols-2 gap-2 mb-4">
        <div class="bg-slate-50 rounded-lg p-2.5"><p class="text-xs text-slate-500">Promedio</p><p class="text-sm font-semibold text-slate-900">${d.promedio} / 5.0</p></div>
        <div class="bg-slate-50 rounded-lg p-2.5"><p class="text-xs text-slate-500">Semestre</p><p class="text-sm font-semibold text-slate-900">${d.semestre}</p></div>
        <div class="bg-slate-50 rounded-lg p-2.5"><p class="text-xs text-slate-500">Postulado</p><p class="text-sm font-semibold text-slate-900">${d.fecha}</p></div>
        <div class="bg-slate-50 rounded-lg p-2.5"><p class="text-xs text-slate-500">Programa</p><p class="text-sm font-semibold text-slate-900">${d.programa}</p></div>
      </div>

      <div class="flex items-center gap-3 p-3 bg-red-50 border border-red-200 rounded-xl mb-4">
        <span class="text-red-500 text-xl">📄</span>
        <div class="flex-1 min-w-0">
          <p class="text-xs font-semibold text-slate-800 truncate">${d.cv || "hoja_de_vida.pdf"}</p>
          <p class="text-xs text-slate-500">Hoja de vida del estudiante</p>
        </div>
        <button type="button" class="text-xs font-bold text-white bg-red-600 hover:bg-red-700 px-3 py-1.5 rounded-lg flex-shrink-0">⬇ Descargar</button>
      </div>

      <div class="bg-amber-50 border border-amber-200 rounded-lg p-3 mb-4">
        <p class="text-xs text-amber-800"><strong>🔒 Nota:</strong> Información compartida por el tutor universitario. Confidencial, solo para el proceso de selección.</p>
      </div>

      ${botones}
    </div>`;

  panel.querySelector("#detalleCerrar")?.addEventListener("click", () => {
    fila.classList.remove("is-selected");
    panel.classList.add("hidden");
    document.getElementById("layoutPostulantes").style.gridTemplateColumns = "1fr";
  });
  panel.querySelector("#detalleAceptar")?.addEventListener("click", () => cambiarEstado(fila, "Aceptado"));
  panel.querySelector("#detalleRechazar")?.addEventListener("click", () => cambiarEstado(fila, "Rechazado"));
}


/* ============================================================
   3. EVALUACIÓN (evaluacion.html)
   ============================================================ */
function initEvaluacion() {
  const filas = document.querySelectorAll(".criterio-row");
  if (!filas.length) return;

  const btnEnviar = document.getElementById("btnEnviarEvaluacion");

  filas.forEach((fila) => {
    const hidden = fila.querySelector('input[type="hidden"]');
    fila.querySelectorAll(".eval-btn").forEach((btn) => {
      btn.addEventListener("click", () => {
        const valor = btn.dataset.valor; // "true" o "false"
        hidden.value = valor;

        fila.querySelectorAll(".eval-btn").forEach((b) => {
          b.classList.remove("is-yes", "is-no");
        });
        btn.classList.add(valor === "true" ? "is-yes" : "is-no");

        actualizarResumenEvaluacion(filas, btnEnviar);
      });
    });
  });
}

function actualizarResumenEvaluacion(filas, btnEnviar) {
  const total = filas.length;
  let cumplidos = 0;
  let noCumplidos = 0;

  filas.forEach((fila) => {
    const val = fila.querySelector('input[type="hidden"]').value;
    if (val === "true") cumplidos++;
    else if (val === "false") noCumplidos++;
  });

  const respondidos = cumplidos + noCumplidos;
  const sinResponder = total - respondidos;
  const pct = Math.round((cumplidos / total) * 100);

  document.getElementById("numRespondidos").textContent = respondidos;
  document.getElementById("barraProgreso").style.width = `${(respondidos / total) * 100}%`;
  document.getElementById("numCumplidos").textContent = cumplidos;
  document.getElementById("numNoCumplidos").textContent = noCumplidos;
  document.getElementById("numSinResponder").textContent = sinResponder;

  const cardPct = document.getElementById("cardCumplimiento");
  if (respondidos > 0) {
    cardPct.classList.remove("hidden");
    const pctEl = document.getElementById("pctCumplimiento");
    pctEl.textContent = `${pct}%`;
    pctEl.className = `text-3xl font-bold font-display ${pct >= 70 ? "text-emerald-600" : "text-red-500"}`;
    document.getElementById("labelCumplimiento").textContent =
      pct >= 80 ? "Sobresaliente" : pct >= 60 ? "Aceptable" : "Requiere mejora";
  } else {
    cardPct.classList.add("hidden");
  }

  btnEnviar.disabled = respondidos < total;
}


/* ============================================================
   4. MIS PRÁCTICAS (practicas.html)
   ============================================================ */
function initMisPracticas() {
  document.querySelectorAll(".btn-pausar").forEach((btn) => {
    btn.addEventListener("click", () => {
      const id = btn.dataset.id;
      const confirmar = confirm("¿Pausar esta práctica? Dejará de ser visible para los estudiantes.");
      if (!confirmar) return;

      // TODO backend: fetch(`/company/practicas/${id}/pausar`, { method: 'POST' })
      const fila = btn.closest("tr");
      const badge = fila.querySelector("td:nth-child(6) span");
      if (badge) {
        badge.textContent = "Pausada";
        badge.className = "text-xs font-medium px-2 py-0.5 rounded-full border bg-slate-100 text-slate-500 border-slate-200";
      }
      btn.textContent = "Reactivar";
    });
  });
}
