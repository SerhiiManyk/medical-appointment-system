import { useState } from "react";

function Register({ language, translations }) {
  return (
    <div>
      <h1>{translations[language].register}</h1>
    </div>
  );
}

export default Register;
