import { useState } from "react";

function EditProfile({ language, translations, onBack }) {
  return (
    <div>
      <h1>{translations[language].editProfile}</h1>
      <button onClick={onBack}>Back</button>
    </div>
  );
}

export default EditProfile;
