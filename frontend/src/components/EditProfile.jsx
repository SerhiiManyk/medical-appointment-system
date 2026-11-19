import { useState } from "react";

function EditProfile({ user, language, translations, onBack }) {
  const [firstName, setFirstName] = useState(user.firstName);
  const [lastName, setLastName] = useState(user.lastName);
  const [email, setEmail] = useState(user.email);
  const [phone, setPhone] = useState(user.phoneNumber);
  const [gender, setGender] = useState(user.gender);
  const [dateOfBirth, setDateOfBirth] = useState(user.dateOfBirth);
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();

    if (oldPassword !== "" || newPassword !== "" || confirmPassword !== "") {
      if (oldPassword === "" || newPassword === "" || confirmPassword === "") {
        if (oldPassword === "") {
          setError("Please giv your old password");
          return;
        } else if (newPassword === "") {
          setError("Please create new password");
          return;
        } else if (confirmPassword === "") {
          setError("Please repeat your new password");
          return;
        }
      }
      if (newPassword !== confirmPassword) {
        setError("Check yor new password one more time.");
        return;
      }
    }
  }

  return (
    <div>
      <h1>{translations[language].editProfile}</h1>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="First name"
          value={firstName}
          onChange={(event) => {
            setFirstName(event.target.value);
            setError("");
          }}
        />
        <input
          type="text"
          placeholder="Last name"
          value={lastName}
          onChange={(event) => {
            setLastName(event.target.value);
            setError("");
          }}
        />
        <input
          type="email"
          id="email"
          name="email"
          placeholder="Enter your email"
          value={email}
          onChange={(event) => {
            setEmail(event.target.value);
            setError("");
          }}
        />
        <input
          type="tel"
          placeholder="Your phone number"
          value={phone}
          onChange={(event) => {
            setPhone(event.target.value);
            setError("");
          }}
        />
        <select
          value={gender}
          onChange={(event) => {
            setGender(event.target.value);
            setError("");
          }}
        >
          <option value="">Change your gender</option>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
        </select>
        <input
          type="date"
          placeholder="Date of Birth"
          value={dateOfBirth}
          onChange={(event) => {
            setDateOfBirth(event.target.value);
            setError("");
          }}
        />
        <input
          type="password"
          id="oldPassword"
          name="oldPassword"
          placeholder="Your old password"
          value={oldPassword}
          onChange={(event) => {
            setOldPassword(event.target.value);
            setError("");
          }}
        />
        <input
          type="password"
          id="newPassword"
          name="newPassword"
          placeholder="Create new password"
          value={newPassword}
          onChange={(event) => {
            setNewPassword(event.target.value);
            setError("");
          }}
        />
        <input
          type="password"
          id="confirmPassword"
          name="confirmPassword"
          placeholder="Confirm your new password"
          value={confirmPassword}
          onChange={(event) => {
            setConfirmPassword(event.target.value);
            setError("");
          }}
        />
        <button type="submit">Save changes</button>
      </form>

      <button onClick={onBack}>Back</button>
    </div>
  );
}

export default EditProfile;
