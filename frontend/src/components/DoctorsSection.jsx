import { useState, useEffect } from "react";

function DoctorsSection({ language, translations }) {
  const [doctorList, setDoctorList] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/doctors")
      .then((response) => response.json())
      .then((doctors) => {
        setDoctorList(doctors);
      })
      .catch((error) => {
        console.error("Error fetching doctors:", error);
      });
  }, []);

  return (
    <section>
      <h2>{translations[language].doctorsTitle}</h2>

      {doctorList.map((doctor) => (
        <article key={doctor.id}>
          <h3>
            {doctor.firstName} {doctor.lastName}
          </h3>
          <h4>{doctor.specialization}</h4>
        </article>
      ))}
    </section>
  );
}

export default DoctorsSection;
