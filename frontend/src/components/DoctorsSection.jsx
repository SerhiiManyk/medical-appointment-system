import { useState, useEffect } from "react";

function DoctorsSection({ language, translations }) {
  const [doctorList, setDoctorList] = useState([]);
  useEffect(() => {
    // Тут буде запит до backend
  }, []);
  return (
    <section>
      <h2>{translations[language].doctorsTitle} </h2>
      <article>
        <h3>John Smith</h3>
      </article>
    </section>
  );
}

export default DoctorsSection;
