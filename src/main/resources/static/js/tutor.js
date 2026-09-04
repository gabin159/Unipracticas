/* ============================================================
   tutor.js
   Interactividad del panel de TUTOR (vanilla JS).
   Mismo criterio que company.js y student.js.
   ============================================================ */

document.addEventListener("DOMContentLoaded", () => {
  initAuthTabsTutor();
  initPerfilTutor();
  initBuscarEstudiantes();
  initTabsSeguimiento();
  initPanelesClicables(); // evaluaciones y empresas comparten el mismo patrón
  initConvenios();
});


/* ============================================================
   1. LOGIN / REGISTRO (login.html)
   ============================================================ */
function initAuthTabsTutor() {
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
function initPerfilTutor() {
  const btn = document.getElementById("btnEditarPerfil");
  if (!btn) return;

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

    document.getElementById("btnAgregarArea")?.classList.toggle("hidden", editando);

    if (editando) {
      btn.textContent = "✏️ Editar";
      btn.classList.remove("bg-emerald-600", "hover:bg-emerald-700");
      btn.classList.add("bg-violet-700", "hover:bg-violet-800");
      btn.dataset.editando = "false";
      // TODO backend: enviar #formDatosTutor con fetch/POST
    } else {
      btn.textContent = "✓ Guardar cambios";
      btn.classList.remove("bg-violet-700", "hover:bg-violet-800");
      btn.classList.add("bg-emerald-600", "hover:bg-emerald-700");
      btn.dataset.editando = "true";
    }
  });
}


/* ============================================================
   3. MIS ESTUDIANTES (estudiantes.html)
   ============================================================ */
function initBuscarEstudiantes() {
  const input = document.getElementById("buscarEstudiante");
  if (!input) return;

  const filas = document.querySelectorAll(".fila-estudiante");
  const filaSinResultados = document.getElementById("filaSinResultados");

  input.addEventListener("input", () => {
    const texto = input.value.trim().toLowerCase();
    let visibles = 0;

    filas.forEach((fila) => {
      const coincide =
        texto === "" ||
        fila.dataset.nombre.includes(texto) ||
        fila.dataset.empresa.includes(texto);
      fila.classList.toggle("hidden", !coincide);
      if (coincide) visibles++;
    });

    filaSinResultados.classList.toggle("hidden", visibles > 0);
  });
}


/* ============================================================
   4. SEGUIMIENTO CON PESTAÑAS (seguimiento.html)
   ============================================================ */
function initTabsSeguimiento() {
  const tabs = document.querySelectorAll(".student-tab");
  if (!tabs.length) return;

  tabs.forEach((tab) => {
    tab.addEventListener("click", () => {
      tabs.forEach((t) => t.classList.remove("is-active"));
      tab.classList.add("is-active");

      document.querySelectorAll(".panel-estudiante").forEach((panel) => {
        panel.classList.toggle("hidden", panel.id !== tab.dataset.target);
      });
    });
  });
}


/* ============================================================
   5. PANELES CLICABLES (evaluaciones.html y empresas.html)
   Mismo patrón: fila/tarjeta con data-target -> muestra el panel
   de detalle con ese id; botón .btn-cerrar-detalle lo oculta.
   ============================================================ */
function initPanelesClicables() {
  const disparadores = document.querySelectorAll(".row-clickable[data-target]");
  if (!disparadores.length) return;

  disparadores.forEach((el) => {
    el.addEventListener("click", () => {
      const targetId = el.dataset.target;
      const panel = document.getElementById(targetId);
      if (!panel) return;

      const yaAbierto = !panel.classList.contains("hidden") && el.classList.contains("is-selected");

      // Cierra todos los paneles y quita la selección de todas las filas
      document.querySelectorAll(".detalle-evaluacion, .detalle-empresa").forEach((p) => p.classList.add("hidden"));
      disparadores.forEach((d) => d.classList.remove("is-selected"));

      // Ajusta el layout de la grilla (1 columna sin panel, 2 con panel)
      const layout = document.getElementById("layoutEvaluaciones") || document.getElementById("layoutEmpresas");

      if (!yaAbierto) {
        panel.classList.remove("hidden");
        el.classList.add("is-selected");
        if (layout) layout.style.gridTemplateColumns = "2fr 1fr";
      } else if (layout) {
        layout.style.gridTemplateColumns = "1fr";
      }
    });
  });

  document.querySelectorAll(".btn-cerrar-detalle").forEach((btn) => {
    btn.addEventListener("click", () => {
      btn.closest(".detalle-evaluacion, .detalle-empresa")?.classList.add("hidden");
      disparadores.forEach((d) => d.classList.remove("is-selected"));
      const layout = document.getElementById("layoutEvaluaciones") || document.getElementById("layoutEmpresas");
      if (layout) layout.style.gridTemplateColumns = "1fr";
    });
  });
}


/* ============================================================
   6. GESTIONAR CONVENIOS (convenios.html)
   ============================================================ */
function initConvenios() {
  const form = document.getElementById("formConvenio");
  if (!form) return;

  const resultado = document.getElementById("resultadoConvenio");
  const btnMostrarPassword = document.getElementById("btnMostrarPassword");
  const passwordGenerada = document.getElementById("passwordGenerada");
  const PASSWORD_DEMO = "UST#Emp2026!";

  form.addEventListener("submit", (e) => {
    // Igual que en React: al enviar, mostramos las credenciales
    // generadas al instante en vez de solo recargar la página.
    e.preventDefault();

    const nombre = document.getElementById("empresaNombre").value.trim() || "la empresa";
    const correo = document.getElementById("empresaCorreo").value.trim();

    document.getElementById("nombreEmpresaCreada").textContent = nombre;
    document.getElementById("correoGenerado").textContent =
      correo || `practicas@${nombre.toLowerCase().replace(/\s+/g, "")}.com`;
    passwordGenerada.textContent = "••••••••••••";
    passwordGenerada.dataset.oculta = "true";
    btnMostrarPassword.textContent = "Mostrar";

    form.classList.add("hidden");
    resultado.classList.remove("hidden");

    // TODO backend: enviar el formulario real y generar la contraseña en el servidor
    // fetch(form.action, { method: 'POST', body: new FormData(form) })
  });

  btnMostrarPassword.addEventListener("click", () => {
    const oculta = passwordGenerada.dataset.oculta !== "false";
    passwordGenerada.textContent = oculta ? PASSWORD_DEMO : "••••••••••••";
    passwordGenerada.dataset.oculta = oculta ? "false" : "true";
    btnMostrarPassword.textContent = oculta ? "Ocultar" : "Mostrar";
  });

  document.getElementById("btnCopiarCredenciales").addEventListener("click", () => {
    const correo = document.getElementById("correoGenerado").textContent;
    const texto = `Correo: ${correo}\nContraseña: ${PASSWORD_DEMO}`;
    navigator.clipboard?.writeText(texto);
  });

  document.getElementById("btnRegistrarOtra").addEventListener("click", () => {
    form.reset();
    form.classList.remove("hidden");
    resultado.classList.add("hidden");
  });
}
