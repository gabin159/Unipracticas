CRUD de Productos (Spring Boot + PostgreSQL)
Sistema web CRUD (Create, Read, Update, Delete) para la gestión e inventario de productos. Permite registrar, listar, filtrar, editar y eliminar productos mediante una interfaz dinámica desarrollada con Java, Spring Boot y Thymeleaf.

📌 Arquitectura del Proyecto
El proyecto sigue una arquitectura en capas basada en el patrón MVC (Modelo-Vista-Controlador):

Controlador (controller): Gestiona las peticiones HTTP y conecta la vista con la lógica de negocio.

Modelo (model): Define la entidad Producto y su estructura de datos.

Repositorio (repository): Interfaz que extiende de Spring Data JPA para las operaciones en la base de datos.

Servicio (service): Contiene la lógica de negocio del sistema.

Vistas (templates / static): Plantillas HTML con Thymeleaf y hojas de estilo CSS para la interfaz de usuario.

✨ Funcionalidades
Registrar: Alta de nuevos productos en el sistema.

Listar: Visualización tabular de todo el inventario registrado.

Buscar: Búsqueda dinámica de productos por nombre.

Editar: Modificación de la información existente de un producto.

Eliminar: Borrado físico de registros de la base de datos.

🚀 Despliegue e Instalación
Clonar el repositorio:

Bash
git clone https://github.com/gabin159/crudProductos.git
cd crudProductos
Ejecutar la aplicación:

Si usas el wrapper de Maven incluido en el proyecto:

Bash
./mvnw clean spring-boot:run
(En Windows CMD / PowerShell usa mvnw clean spring-boot:run)

Si tienes Maven instalado globalmente en el sistema:

Bash
mvn spring-boot:run
Acceder a la aplicación:
Abre el navegador web e ingresa a:
http://localhost:8080

📝 Justificación del Trabajo
Para este proyecto utilicé Spring Boot porque facilita el desarrollo de aplicaciones web mediante una estructura organizada y herramientas que reducen la configuración manual. Spring Data JPA simplifica la persistencia y el acceso a la base de datos, mientras que Thymeleaf permite renderizar e integrar fácilmente la lógica del backend directamente en el HTML.

La separación en MVC (Modelo, Vista, Controlador) garantiza que la lógica de negocio, la persistencia de datos y la interfaz de usuario se mantengan independientes, facilitando la escalabilidad y el mantenimiento del código.

Uno de los mayores retos durante el desarrollo fue sincronizar la entidad Java con PostgreSQL tras modificar algunos atributos del modelo, sumado al mapeo de las rutas en el controlador para coordinar correctamente las acciones de guardado, actualización y eliminación. Este proyecto permitió consolidar conocimientos clave en desarrollo backend con Java, persistencia de datos con JPA y gestión de versiones con Git.

👥 Autores
Fredy Borda

Nicolás Díaz

Universidad Santo Tomás — Ingeniería de Sistemas