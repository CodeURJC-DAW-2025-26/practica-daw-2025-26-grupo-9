# eQuis Social Network

## 👥 Miembros del Equipo
| Nombre y Apellidos | Correo URJC | Usuario GitHub |
|:--- |:--- |:--- |
| David Paul Limaylla Ticlavilca | dp.limaylla.2021@alumnos.urjc.es | DavidPaul-LT |
| Elena Ceinos Abeijón  | e.ceinos@alumnos.urjc.es | elenacabe |
| Sergio Rodríguez Gil | s.rodriguezgi.2021@alumnos.urjc.es | NeoDaru |
| Alvaro Sepulveda Crespo | a.sepulvedac.2018@alumnos.urjc.es | alvaro-sepu |
| Cassiel Seth Mayorca Heirisman | cs.mayorca.2018@alumnos.urjc.es | Cassiel123 |

---

## 🎭 **Preparation 1: Project Description**

### **Descripción del Tema**
This project consists of a web-based social network focused on user interaction through posts and comments, allowing users to express their ideas and communicate with others on the platform.

Users can create text-based posts with optional images, comment on posts, and interact through likes. Content is organized into fixed categories managed by the administrator and dynamic tags created by users, providing structured yet flexible navigation.
A general feed prioritizes posts based on user interaction and recency, improving content visibility and engagement.

### **Entities**

1. **[Entidad 1]**: User
1. **[Entidad 3]**: Post
1. **[Entidad 4]**: Comment
1. **[Entidad 5]**: Category


**Relaciones entre entidades:**
- [User – Post] A user can create 0..n posts, and each post belongs to a single user (1:N).
- [User – Comment] A user can create 0..n comments, and each comment belongs to a single user (1:N).
- [Post – Comment] A post can have 0..n comments, and each comment belongs to a single post (1:N).
- [Category – Post] A category can contain 0..n posts, and each post belongs to a single category (1:N).

### **User Permissions**

* **Anonymous User**: 
  - Permissions: View posts and comments, register in the application, 
  - Owns no entities

* **Registered User**: 
  - Permissions: Create posts, Comment on posts, Edit their profile (image, name, and biography, password), Edit their own comments, Delete their own posts and comments, Give and remove likes from posts
  - Owns: Their profile, Their posts, Their comments, Their likes

* **Admins**: 
  - Permissions: Block and unblock users, Delete any post, Delete any comment, View global application statistics. Inherits the functions of the registered user, create, delete, and modify category
  - Owns: Inherits the owns of the registered user

### **Images**

- **[Entidad con imágenes 1]**: User
- **[Entidad con imágenes 2]**: Post
- **[Entidad con imágenes 3]**: Comment
- **[Entidad con imágenes 4]**: Category

### **Charts**

- **Chart 1**: Posts with the most likes — Bar chart

### **Complementary Technology**
- Automatic email sending (e.g., registration confirmation or user block notification) using JavaMailSender

### **Algorithm or Advanced Query**

- **Post Ranking Algorithm**: The general feed and category feeds are generated using a scoring algorithm based on user interaction and time. Score = (Likes × 3) + (Comments × 2) − (Hours since creation × 0.1)

- **Description**: This algorithm prioritizes posts with higher interaction (likes and comments) while still considering recency. As a result, relevant and active posts gain visibility without excluding newly created content. Deleted posts are excluded from the feed.


---

## 🛠 **Preparación 2: Maquetación de páginas con HTML y CSS**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navigation Diagram**
Diagram showing how to navigate between the different pages of the application:

![Diagrama de Navegación](images/navigation-diagram.jpg)

> [Descripción opcional del flujo de navegación: Ej: "El usuario puede acceder desde la página principal a todas las secciones mediante el menú de navegación. Los usuarios anónimos solo tienen acceso a las páginas públicas, mientras que los registrados pueden acceder a su perfil y panel de usuario."]

### **Capturas de Pantalla y Descripción de Páginas**

#### **1. Página Principal / Home**
![Página Principal](images/home-page.png)

> [Descripción breve: Ej: "Página de inicio que muestra los productos destacados, categorías principales y un banner promocional. Incluye barra de navegación y acceso a registro/login para usuarios no autenticados."]

#### **AQUÍ AÑADIR EL RESTO DE PÁGINAS**

---

## 🛠 **Práctica 1: Web con HTML generado en servidor y AJAX**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Navegación y Capturas de Pantalla**

#### **Diagrama de Navegación**

Solo si ha cambiado.

#### **Capturas de Pantalla Actualizadas**

Solo si han cambiado.

### **Instrucciones de Ejecución**

#### **Requisitos Previos**
- **Java**: versión 21 o superior
- **Maven**: versión 3.8 o superior
- **MySQL**: versión 8.0 o superior
- **Git**: para clonar el repositorio

#### **Pasos para ejecutar la aplicación**

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]
   ```

2. **AQUÍ INDICAR LO SIGUIENTES PASOS**

#### **Credenciales de prueba**
- **Usuario Admin**: usuario: `admin`, contraseña: `admin`
- **Usuario Registrado**: usuario: `user`, contraseña: `user`

### **Database Entities Diagram**

Entity-Relationship diagram showing entities, attributes, and relationships:

![Entity-Relationship Diagram](images/database-diagram.png)

### **Diagrama de Clases y Templates**

Diagrama de clases de la aplicación con diferenciación por colores o secciones:

![Diagrama de Clases](images/classes-diagram.png)

> [Descripción opcional del diagrama y relaciones principales]

### **Participación de Miembros en la Práctica 1**

#### **Alumno 1 - [David Paul Limaylla Ticlavilca]**

[Levantar la bbdd y funcionalidades del administrador (bloquear usuario, eliminar post, eliminar comentario, crear categoria, editar categoria, eliminar categoria) - (categorias y post) views user]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [admin option added - category/users images loaded](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/a8de5af018e1076f4465e934b9f7788ee0a052d3)  | [AdminController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/a8de5af018e1076f4465e934b9f7788ee0a052d3#diff-d377b68ab6e6f136a5136f6bf045b57c85a6c1bf0e507afb757e1e49b6c29bae)   |
|2| [entities updated and properties to run database, dataloader](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/92dd6eb79894af0e0b4572d6658ee68cdc1193d1)  | [DataLoader.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/92dd6eb79894af0e0b4572d6658ee68cdc1193d1#diff-6e0714325580c329886beb90a7071653f93d4afd9ff1d90a5ea78cd2e19caa57)   |
|3| [post view added - return safePath method added](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/7ba9811223dd2819749310bb9952de337b81db66)  | [PostController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/7ba9811223dd2819749310bb9952de337b81db66#diff-0da083eb305b2939c35d4c6d4bb11d3c63b8af5fecd980cbafde538a7f3bdca7)   |
|4| [admin can delete from admin's page](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/20f26b7ac3a2e417d4ff11c5367dfda97c692ff0)  | [AdminController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/20f26b7ac3a2e417d4ff11c5367dfda97c692ff0#diff-d377b68ab6e6f136a5136f6bf045b57c85a6c1bf0e507afb757e1e49b6c29bae)   |
|5| [categories page fixed - image problem solved](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/69ecbd057ad157858ee5386b787cd9c7b230b4cb)  | [CategoryService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/69ecbd057ad157858ee5386b787cd9c7b230b4cb#diff-cded6e6896208787d6638e09428efe71138f30e4fbf2f26c5cf463a0e45a3935)   |

---

#### **Alumno 2 - [Elena Ceinos abeijón]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Levantar Spring con maven](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/3a0f64a0b60036c0c6c0fd1290d7f4bd01a0a6ac)  | [Archivo1](URL_archivo_1)   |
|2| [Basic controllers,security and mustache ](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/23b5eceb13a154a57b792596d9ffdb5445b85833) | [Archivo2](URL_archivo_2)   |
|3| [Encrypted pass](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/be05b165ff4c1d0cfbe4aa4f386233c9d611aa3e)  | [Archivo3](URL_archivo_3)   |
|4| [User controller to use service instead of repo]([URL_commit_4](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/0baab265c178a8b0e252e9ffb92f46c637d88a82))  | [Archivo4](URL_archivo_4)   |
|5| [Keystore and port changed]([URL_commit_5](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/a18f80a9da77da081ad270afb3943a83a49cac93))  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - Alvaro Sepúlveda Crespo**

Implemetacion del la tecnologia avanzada(grafico), algoritmo de ordenacion, comentarios y categorias.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| Algoritmo de ordenacion de post (https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/653ddbbe04218c0b467e5d8ec2feaad85deeaa95)  | [Archivo1](URL_archivo_1)   |
|2| Home controller changes for chart(https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/a57771f9d4e0df223c44ef26f44be4ffaa1996c0)  | [Archivo2](URL_archivo_2)   |
|3| Controller changes for comments(https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/49cdb7942b2bab35629a7d5b431c15c0107f72ea)  | [Archivo3](URL_archivo_3)   |
|4| Global controller advice change for categories(https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/49cdb7942b2bab35629a7d5b431c15c0107f72ea)  | [Archivo4](URL_archivo_4)   |
|5| fix for categories controller(https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/aae11f368baef394a5af3c06e8cee4ca2bc21a32)  | [Archivo5](URL_archivo_5)   |

---

#### **Student 4 - Sergio Rodríguez Gil**

In addition to general improvements, I was responsible for implementing the like functionality, anonymous user support, and the email notification system triggered upon user registration.

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Implement email when registering](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/2a47bb86e06fd37659b784adb9d0512229784960)  | [EmailService.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/blob/main/backend/equis/src/main/java/es/urjc/daw/equis/service/EmailService.java)   |
|2| [Implemented anonymous user functionalities](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/b759f7955bc4381611a7ad08ce3624b71207877b)  | [HomeController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/blob/main/backend/equis/src/main/java/es/urjc/daw/equis/controller/HomeController.java)   |
|3| [Create PostController](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/23df9ec1ac04559ee73aa56b1c7c087546eabd49)  | [PostController.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/blob/main/backend/equis/src/main/java/es/urjc/daw/equis/controller/PostController.java)   |
|4| [Create LikeRepository.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/92ca883afdc37f2eb7cdb490285bca0dfa8f9fbc)  | [welcome.html](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/blob/main/backend/equis/src/main/resources/templates/welcome.html)   |
|5| [Create like entity](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/commit/de5cf7c223372dbe743bb82c318eab982ba77077)  | [Like.java](https://github.com/CodeURJC-DAW-2025-26/practica-daw-2025-26-grupo-9/blob/main/backend/equis/src/main/java/es/urjc/daw/equis/model/Like.java)   |

---

## 🛠 **Práctica 2: Incorporación de una API REST a la aplicación web, despliegue con Docker y despliegue remoto**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](https://www.youtube.com/watch?v=x91MPoITQ3I)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Documentación de la API REST**

#### **Especificación OpenAPI**
📄 **[Especificación OpenAPI (YAML)](/api-docs/api-docs.yaml)**

#### **Documentación HTML**
📖 **[Documentación API REST (HTML)](https://raw.githack.com/[usuario]/[repositorio]/main/api-docs/api-docs.html)**

> La documentación de la API REST se encuentra en la carpeta `/api-docs` del repositorio. Se ha generado automáticamente con SpringDoc a partir de las anotaciones en el código Java.

### **Diagrama de Clases y Templates Actualizado**

Diagrama actualizado incluyendo los @RestController y su relación con los @Service compartidos:

![Diagrama de Clases Actualizado](images/complete-classes-diagram.png)

### **Instrucciones de Ejecución con Docker**

#### **Requisitos previos:**
- Docker instalado (versión 20.10 o superior)
- Docker Compose instalado (versión 2.0 o superior)

#### **Pasos para ejecutar con docker-compose:**

1. **Clonar el repositorio** (si no lo has hecho ya):
   ```bash
   git clone https://github.com/[usuario]/[repositorio].git
   cd [repositorio]
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**:

### **Construcción de la Imagen Docker**

#### **Requisitos:**
- Docker instalado en el sistema

#### **Pasos para construir y publicar la imagen:**

1. **Navegar al directorio de Docker**:
   ```bash
   cd docker
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**

### **Despliegue en Máquina Virtual**

#### **Requisitos:**
- Acceso a la máquina virtual (SSH)
- Clave privada para autenticación
- Conexión a la red correspondiente o VPN configurada

#### **Pasos para desplegar:**

1. **Conectar a la máquina virtual**:
   ```bash
   ssh -i [ruta/a/clave.key] [usuario]@[IP-o-dominio-VM]
   ```
   
   Ejemplo:
   ```bash
   ssh -i ssh-keys/app.key vmuser@10.100.139.XXX
   ```

2. **AQUÍ LOS SIGUIENTES PASOS**:

### **URL de la Aplicación Desplegada**

🌐 **URL de acceso**: `https://[nombre-app].etsii.urjc.es:8443`

#### **Credenciales de Usuarios de Ejemplo**

| Rol | Usuario | Contraseña |
|:---|:---|:---|
| Administrador | admin | admin123 |
| Usuario Registrado | user1 | user123 |
| Usuario Registrado | user2 | user123 |

### **Participación de Miembros en la Práctica 2**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Elena Ceinos abeijón]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Levantar Spring con maven](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/3a0f64a0b60036c0c6c0fd1290d7f4bd01a0a6ac)  | [Archivo1](URL_archivo_1)   |
|2| [Basic controllers,security and mustache ](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/23b5eceb13a154a57b792596d9ffdb5445b85833) | [Archivo2](URL_archivo_2)   |
|3| [Encrypted pass](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/be05b165ff4c1d0cfbe4aa4f386233c9d611aa3e)  | [Archivo3](URL_archivo_3)   |
|4| [User controller to use service instead of repo]([URL_commit_4](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/0baab265c178a8b0e252e9ffb92f46c637d88a82))  | [Archivo4](URL_archivo_4)   |
|5| [Keystore and port changed]([URL_commit_5](https://github.com/CodeURJC-DAW-2025-26/daw-2025-26-project-base/commit/a18f80a9da77da081ad270afb3943a83a49cac93))  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

## 🛠 **Práctica 3: Implementación de la web con arquitectura SPA**

### **Vídeo de Demostración**
📹 **[Enlace al vídeo en YouTube](URL_del_video)**
> Vídeo mostrando las principales funcionalidades de la aplicación web.

### **Preparación del Entorno de Desarrollo**

#### **Requisitos Previos**
- **Node.js**: versión 18.x o superior
- **npm**: versión 9.x o superior (se instala con Node.js)
- **Git**: para clonar el repositorio

#### **Pasos para configurar el entorno de desarrollo**

1. **Instalar Node.js y npm**
   
   Descarga e instala Node.js desde [https://nodejs.org/](https://nodejs.org/)
   
   Verifica la instalación:
   ```bash
   node --version
   npm --version
   ```

2. **Clonar el repositorio** (si no lo has hecho ya)
   ```bash
   git clone https://github.com/[usuario]/[nombre-repositorio].git
   cd [nombre-repositorio]
   ```

3. **Navegar a la carpeta del proyecto React**
   ```bash
   cd frontend
   ```

4. **AQUÍ LOS SIGUIENTES PASOS**

### **Diagrama de Clases y Templates de la SPA**

Diagrama mostrando los componentes React, hooks personalizados, servicios y sus relaciones:

![Diagrama de Componentes React](images/spa-classes-diagram.png)

### **Participación de Miembros en la Práctica 3**

#### **Alumno 1 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 2 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 3 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

---

#### **Alumno 4 - [Nombre Completo]**

[Descripción de las tareas y responsabilidades principales del alumno en el proyecto]

| Nº    | Commits      | Files      |
|:------------: |:------------:| :------------:|
|1| [Descripción commit 1](URL_commit_1)  | [Archivo1](URL_archivo_1)   |
|2| [Descripción commit 2](URL_commit_2)  | [Archivo2](URL_archivo_2)   |
|3| [Descripción commit 3](URL_commit_3)  | [Archivo3](URL_archivo_3)   |
|4| [Descripción commit 4](URL_commit_4)  | [Archivo4](URL_archivo_4)   |
|5| [Descripción commit 5](URL_commit_5)  | [Archivo5](URL_archivo_5)   |

