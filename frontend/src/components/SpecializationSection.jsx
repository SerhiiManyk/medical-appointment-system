import { useState, useEffect } from "react";

const specializationList = [
  { name: "Family Doctor", image: "/images/familydoctor-iconjpg.jpg" },
  { name: "Cardiologist", image: "/images/cardiologist-ikon.jpg" },
  { name: "Dentist", image: "/images/dentist-icon.jpg" },
  { name: "Surgeon", image: "/images/surgery_ikon.jpg" },
  { name: "Dermatologist", image: "/images/dermatolog-icon.jpg" },
  { name: "Pediatrician", image: "/images/pediatrican_ikon.jpg" },
  { name: "Neurologist", image: "/images/neurologist_ikon.jpg" },
];

function SpecializationSection({ language, translations }) {
  const [doctorList, setDoctorList] = useState([]);
  const [selectedSpecialization, setSelectedSpecialization] = useState(null);

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
      <h1 className="specializations-title">
        {translations[language].specializationsTitle}
      </h1>

      <div className="specializations-grid">
        {specializationList.map((specialization) => (
          <article key={specialization.name}>
            <img src={specialization.image} alt={specialization.name} />
            <h4>{specialization.name}</h4>
          </article>
        ))}
      </div>
    </section>
  );
}

export default SpecializationSection;
