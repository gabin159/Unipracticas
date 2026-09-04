
document.addEventListener("DOMContentLoaded", () => {
  initAuthTabs();
  initPerfilEstudiante();
  initBuscarPracticas();
  initPracticaDetalle();
  initSeguimiento();
});
 
 
/* ============================================================
   1. LOGIN / REGISTRO (login.html)
   ============================================================ */
function initAuthTabs() {
  const tabLogin = document.getElementById("tabLogin");
  const tabRegister = document.getElementById("tabRegister");
  const formLogin = document.getElementById("formLogin");
  const formRegister = document.getElementById("formRegister");
  if (!tabLogin) return;
 
  const mostrarLogin = () => {
    tabLogin.classList.add("is-active");
    tabRegister.classList.remove("is-active");
    formLogin.classList.remove("hidden");
    formRegister.classList.add("hidden");
  };
  const mostrarRegistro = () => {
    tabRegister.classList.add("is-active");
    tabLogin.classList.remove("is-active");
    formRegister.classList.remove("hidden");
    formLogin.classList.add("hidden");
  };
 
  tabLogin.addEventListener("click", mostrarLogin);
  tabRegister.addEventListener("click", mostrarRegistro);
  document.getElementById("irARegistro")?.addEventListener("click", mostrarRegistro);
  document.getElementById("irALogin")?.addEventListener("click", mostrarLogin);
 
  // Validación simple de confirmación de contraseña antes de enviar el registro
  formRegister?.addEventListener("submit", (e) => {
    const pass = document.getElementById("passwordRegistro").value;
    const confirm = document.getElementById("passwordConfirmacion").value;
    const error = document.getElementById("errorConfirmacion");
    if (pass !== confirm) {
      e.preventDefault();
      error.classList.remove("hidden");
    } else {
      error.classList.add("hidden");
    }
  });
}
 
 
/* ============================================================
   2. PERFIL (perfil.html)
   ============================================================ */
function initPerfilEstudiante() {
  const btn = document.getElementById("btnEditarPerfil");
  if (btn) {
    btn.addEventListener("click", () => {
      const campos = document.querySelectorAll(".campo-perfil");
      const editando = btn.dataset.editando === "true";
 
      campos.forEach((input) => {
        input.disabled = editando;
        input.classList.toggle("border", !editando);
        input.classList.toggle("border-slate-200", !editando);
        input.classList.toggle("rounded-lg", !editando);
        input.classList.toggle("px-2", !editando);
        input.classList.toggle("py-1", !editando);
      });
 
      if (editando) {
        btn.textContent = "✏️ Editar";
        btn.classList.remove("bg-emerald-600", "hover:bg-emerald-700");
        btn.classList.add("bg-blue-700", "hover:bg-blue-800");
        btn.dataset.editando = "false";
        // TODO backend: enviar #formDatosPersonales por fetch/POST
      } else {
        btn.textContent = "✓ Guardar cambios";
        btn.classList.remove("bg-blue-700", "hover:bg-blue-800");
        btn.classList.add("bg-emerald-600", "hover:bg-emerald-700");
        btn.dataset.editando = "true";
      }
    });
  }
 
  // Hoja de vida: eliminar / subir
  document.getElementById("btnEliminarCv")?.addEventListener("click", () => {
    document.getElementById("cvConArchivo").classList.add("hidden");
    document.getElementById("cvSinArchivo").classList.remove("hidden");
    // TODO backend: DELETE /student/hoja-de-vida
  });
 
  document.getElementById("inputCv")?.addEventListener("change", (e) => {
    if (!e.target.files.length) return;
    document.getElementById("cvSinArchivo").classList.add("hidden");
    document.getElementById("cvConArchivo").classList.remove("hidden");
    // TODO backend: subir el archivo real con FormData
  });
}
 
 
/* ============================================================
   3. BUSCAR PRÁCTICAS (buscar-practicas.html)
   ============================================================ */
function initBuscarPracticas() {
  const input = document.getElementById("filtroTexto");
  if (!input) return;
 
  const selectArea = document.getElementById("filtroArea");
  const selectModalidad = document.getElementById("filtroModalidad");
  const tarjetas = document.querySelectorAll(".practica-card");
  const contador = document.getElementById("contadorResultados");
  const sinResultados = document.getElementById("sinResultados");
 
  const aplicarFiltros = () => {
    const texto = input.value.trim().toLowerCase();
    const area = selectArea.value;
    const modalidad = selectModalidad.value;
    let visibles = 0;
 
    tarjetas.forEach((card) => {
      const coincideTexto =
        texto === "" ||
        card.dataset.titulo.includes(texto) ||
        card.dataset.empresa.includes(texto);
      const coincideArea = area === "Todas" || card.dataset.area === area;
      const coincideModalidad = modalidad === "Todas" || card.dataset.modalidad === modalidad;
 
      const mostrar = coincideTexto && coincideArea && coincideModalidad;
      card.classList.toggle("hidden", !mostrar);
      if (mostrar) visibles++;
    });
 
    contador.textContent = visibles;
    sinResultados.classList.toggle("hidden", visibles > 0);
  };
 
  input.addEventListener("input", aplicarFiltros);
  selectArea.addEventListener("change", aplicarFiltros);
  selectModalidad.addEventListener("change", aplicarFiltros);
}
 
 
/* ============================================================
   4. DETALLE DE PRÁCTICA (practica-detalle.html)
   ============================================================ */
function initPracticaDetalle() {
  const form = document.getElementById("formPostular");
  if (!form) return;
 
  form.addEventListener("submit", (e) => {
    // Igual que en React (setPosted(true)): mostramos la confirmación
    // al instante sin esperar recarga de página.
    e.preventDefault();
 
    document.getElementById("bannerAntes").classList.add("hidden");
    const despues = document.getElementById("bannerDespues");
    despues.classList.remove("hidden");
    despues.classList.add("flex");
 
    // TODO backend: enviar la postulación real
    // fetch(form.action, { method: 'POST' })
  });
}
 
 
/* ============================================================
   5. SEGUIMIENTO (seguimiento.html)
   ============================================================ */
function initSeguimiento() {
  const btnPublicar = document.getElementById("btnPublicarEvidencia");
  if (!btnPublicar) return;
 
  const inputTitulo = document.getElementById("evidenciaTitulo");
  const selectorEmoji = document.getElementById("selectorEmoji");
  const btnAdjuntarFoto = document.getElementById("btnAdjuntarFoto");
  const fotoAdjuntada = document.getElementById("fotoAdjuntada");
  const btnQuitarFoto = document.getElementById("btnQuitarFoto");
  const textareaComentario = document.getElementById("evidenciaComentario");
  const galeria = document.getElementById("galeriaEvidencias");
  const listaComentarios = document.getElementById("listaComentarios");
  const avisoPublicada = document.getElementById("avisoEvidenciaPublicada");
 
  let emojiSeleccionado = "🖥️";
 
  // Habilita el botón "Publicar" solo si hay título
  inputTitulo.addEventListener("input", () => {
    btnPublicar.disabled = inputTitulo.value.trim() === "";
  });
 
  // Selección del tipo de actividad (emoji)
  selectorEmoji.querySelectorAll(".emoji-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      selectorEmoji.querySelectorAll(".emoji-btn").forEach((b) => b.classList.remove("is-selected"));
      btn.classList.add("is-selected");
      emojiSeleccionado = btn.dataset.emoji;
    });
  });
 
  // Simulación de adjuntar/quitar foto
  btnAdjuntarFoto.addEventListener("click", () => {
    btnAdjuntarFoto.classList.add("hidden");
    fotoAdjuntada.classList.remove("hidden");
    fotoAdjuntada.classList.add("flex");
  });
  btnQuitarFoto.addEventListener("click", () => {
    fotoAdjuntada.classList.add("hidden");
    fotoAdjuntada.classList.remove("flex");
    btnAdjuntarFoto.classList.remove("hidden");
  });
 
  // Publicar evidencia: agrega tarjeta a la galería y comentario al diario
  btnPublicar.addEventListener("click", () => {
    const titulo = inputTitulo.value.trim();
    if (!titulo) return;
 
    // Nueva tarjeta en la galería
    const tarjeta = document.createElement("div");
    tarjeta.className = "rounded-xl border border-slate-200 overflow-hidden";
    tarjeta.innerHTML = `
      <div class="h-28 bg-gradient-to-br from-slate-100 to-slate-200 flex items-center justify-center"><span class="text-4xl">${emojiSeleccionado}</span></div>
      <div class="p-2"><p class="text-xs font-medium text-slate-700 leading-tight">${escapeHtml(titulo)}</p></div>`;
    galeria.appendChild(tarjeta);
 
    // Si hay comentario, lo agrega también al diario de avance
    const comentario = textareaComentario.value.trim();
    if (comentario) {
      const entrada = document.createElement("div");
      entrada.className = "relative bg-slate-50 rounded-xl border border-slate-100 p-4";
      entrada.innerHTML = `
        <div class="flex items-center gap-2 mb-2">
          <div class="w-7 h-7 rounded-full bg-blue-600 flex items-center justify-center text-white text-xs font-bold flex-shrink-0">M</div>
          <div class="flex-1 min-w-0"><span class="text-xs font-semibold text-slate-800">Mariana López</span><span class="text-xs text-slate-400 ml-2">Hoy</span></div>
          <span class="text-xs text-emerald-600 font-medium bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full">Publicado</span>
        </div>
        <p class="text-sm text-slate-700 leading-relaxed pl-9">${escapeHtml(comentario)}</p>`;
      listaComentarios.appendChild(entrada);
    }
 
    // Resetea el formulario
    inputTitulo.value = "";
    textareaComentario.value = "";
    fotoAdjuntada.classList.add("hidden");
    fotoAdjuntada.classList.remove("flex");
    btnAdjuntarFoto.classList.remove("hidden");
    btnPublicar.disabled = true;
 
    // Aviso de confirmación por 2.5s (igual que el setTimeout de React)
    avisoPublicada.classList.remove("hidden");
    avisoPublicada.classList.add("flex");
    setTimeout(() => {
      avisoPublicada.classList.add("hidden");
      avisoPublicada.classList.remove("flex");
    }, 2500);
 
    // TODO backend: subir la evidencia real (foto + título + comentario)
    // fetch('/student/evidencias', { method: 'POST', body: new FormData(...) })
  });
}
 
function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str;
  return div.innerHTML;
}