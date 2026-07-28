const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.adminActualizarUsuario = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Debe iniciar sesión");
  }

  const uidObjetivo = data.uid;
  const emailNuevo = data.email;
  const passwordNueva = data.password;

  if (!uidObjetivo || !emailNuevo) {
    throw new functions.https.HttpsError("invalid-argument", "Faltan datos obligatorios");
  }

  const callerDoc = await admin.firestore().collection("usuarios").doc(context.auth.uid).get();
  const callerRol = callerDoc.data()?.rol || "";

  const targetDoc = await admin.firestore().collection("usuarios").doc(uidObjetivo).get();
  if (!targetDoc.exists) {
    throw new functions.https.HttpsError("not-found", "Usuario no encontrado");
  }
  const targetRol = targetDoc.data()?.rol || "";

  const soporte = callerRol === "SOPORTE";
  const gerente = callerRol === "GERENTE";

  if (soporte && targetRol !== "GERENTE" && targetRol !== "EMPLEADO") {
    throw new functions.https.HttpsError("permission-denied", "Sin permisos sobre este rol");
  }
  if (gerente && targetRol !== "EMPLEADO") {
    throw new functions.https.HttpsError("permission-denied", "Gerente solo puede editar empleados");
  }
  if (!soporte && !gerente) {
    throw new functions.https.HttpsError("permission-denied", "Sin permisos de administración");
  }

  const authUpdate = { email: emailNuevo };
  if (passwordNueva && passwordNueva.length >= 6) {
    authUpdate.password = passwordNueva;
  }

  await admin.auth().updateUser(uidObjetivo, authUpdate);
  return { success: true };
});
