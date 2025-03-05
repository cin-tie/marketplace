// "use client";

// import { useEffect, useState } from "react";
// import Button from "@/app/components/Button";
// import Image from "next/image";
// import "tailwindcss/tailwind.css";
// import "@/styles/globals.css";

// export default function Home() {
//   const [hasLoaded, setHasLoaded] = useState(false);

//   useEffect(() => {
//     const timer = setTimeout(() => {
//       setHasLoaded(true);
//     }, 200); // Delay to trigger the animations after page load

//     return () => clearTimeout(timer); // Cleanup timer
//   }, []);

//   return (
//     <div className="relative h-screen">
//       <div
//         className={`absolute inset-0 bg-[url('/images/background-homepage.png')] bg-cover bg-center transition-opacity duration-1000 ${hasLoaded ? "opacity-20" : "opacity-0"}`}
//       ></div>

//       <div className="relative flex flex-col justify-center items-center h-full">
//         <div
//           className={`flex justify-center mt-[-300px] ${hasLoaded ? "animate-fadeInUp" : "opacity-0"}`}
//         >
//           <h1 className="text-4xl font-bold text-[#f5f5f5]">WELCOME</h1>
//         </div>

//         <div
//           className={`flex justify-center mt-10 ${hasLoaded ? "animate-fadeInUp" : "opacity-0"}`}
//         >
//           <h1 className="text-7xl font-bold text-[#534487]">MarketPlace</h1>
//         </div>

//         <div className="flex relative">
//           <div
//             className={`z-0 w-auto ${hasLoaded ? "animate-slideInUp" : "opacity-0"}`}
//           >
//             <Image
//               src="/images/pic1.png"
//               width={300}
//               height={400}
//               alt="Picture 1"
//             />
//           </div>

//           <div
//             className={`absolute z-10 ${hasLoaded ? "animate-slideInRight" : "opacity-0"}`}
//           >
//             <Image
//               src="/images/pic2.png"
//               width={250}
//               height={500}
//               alt="Picture 2"
//             />
//           </div>

//           <div
//             className={`absolute z-5 ${hasLoaded ? "animate-slideInLeft" : "opacity-0"}`}
//           >
//             <Image
//               src="/images/pic3.png"
//               width={220}
//               height={500}
//               alt="Picture 3"
//             />
//           </div>
//         </div>

//         <div
//           className={`flex relative z-20 ${hasLoaded ? "animate-fadeInUpButton" : "opacity-0"}`}
//         >
//           <Button label="Try!" href="/home" variant="big" />
//         </div>
//       </div>
//     </div>
//   );
// }
"use client";

import { useEffect, useState } from "react";
import { z } from "zod";

export default function Home() {
  const [data, setData] = useState("");
  const [login, setLogin] = useState(false);

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [csrf, setCsrf] = useState("");

  const [authError, setAuthError] = useState(false);
  const [error, setError] = useState("");

  const [validationErrors, setValidationErrors] = useState({
    username: "",
    password: "",
  });

  const [rememberMe, setRememberMe] = useState(false);

    const userSchema = z.object({
      username: z
        .string()
        .min(6, "Username must contain more than 6 characters")
        .max(256, "Username must contain less than 256 characters")
        .regex(
          /^[a-zA-Z0-9_]+$/,
          "Username must contain only letters, numbers or symbol underscore",
        ),

      password: z
        .string()
        .min(8, "Password must contain more than 8 characters")
        .max(256, "Password must contain less than 256 characters")
        .regex(/[a-z]/, "Password must contain at least one lowercase letter")
        .regex(/[A-Z]/, "Password must contain at least one uppercase letter")
        .regex(/[0-9]/, "Password must contain at least one number"),
    });

  const validateInputs = () => {
    try {
      userSchema.parse({ username, password });
      setValidationErrors({ username: "", password: "" });
      return true;
    } catch (err) {
      if (err instanceof z.ZodError) {
        const errors = {
          username: err.errors.find((e) => e.path[0] === "username")?.message || "",
          password: err.errors.find((e) => e.path[0] === "password")?.message || "",
        };
        setValidationErrors(errors);
      }
      return false;
    }
  };

  const signin = async () => {
    if (!validateInputs()) return;

    try {
      const response = await fetch("http://localhost:8080/auth/signin", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": csrf,
        },
        body: JSON.stringify({
          username: username,
          password: password,
          rememberme: rememberMe,
        }),
        credentials: "include",
      });
      const result = await response.json();
      if (result.success) {
        window.location.reload();
      } else {
        setAuthError(true);
        setError(result.errors[0]);
      }
    } catch (err) {
      console.error("An error occured: ", err);
      setAuthError(true);
      setError("An error occured while sing-in");
    }
  };

  const signup = async () => {
    if (!validateInputs()) return;

    try {
      const response = await fetch("http://localhost:8080/auth/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": csrf,
        },
        body: JSON.stringify({
          username: username,
          password: password,
          rememberme: rememberMe,
        }),
        credentials: "include",
      });
      const result = await response.json();
      if (result.success) {
        window.location.reload();
      } else {
        setAuthError(true);
        setError(result.errors[0]);
      }
    } catch (err) {
      console.error("An error occured: ", err);
      setAuthError(true);
      setError("An error occured while sing-in");
    }
  };

  const logout = async () => {
    try {
      await fetch("http://localhost:8080/auth/signout", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": csrf,
        },
        credentials: "include",
      });
      window.location.reload();
    } catch (err) {
      console.error("An error occured: ", err);
      setAuthError(true);
      setError("An error occurred during logout");
    }
  };

  const loadData = async () => {
    try {
      const response = await fetch("http://localhost:8080/test", {
        credentials: "include",
      });
      const data = await response.text();
      setData(data);
    } catch (err) {
      console.error("An error occured: ", err);
      setAuthError(true);
      setError("An error occurred while loading data.");
    }
  };

  useEffect(() => {
    const fetchCsrf = async () => {
      try {
        const response = await fetch("http://localhost:8080/auth/csrf", {
          credentials: "include",
        });
        const data = await response.json();
        setCsrf(data.token);
      } catch (err) {
        console.error("An error occured: ", err);
        setAuthError(true);
        setError("An error occurred while fetching CSRF token.");
      }
    };

    const fetchAuthStatus = async () => {
      try {
        const response = await fetch("http://localhost:8080/auth/status", {
          credentials: "include",
        });
        const data = await response.json();
        if (data.auth) {
          loadData();
        } else {
          setLogin(true);
        }
      } catch (err) {
        console.error("An error occured: ", err);
        setAuthError(true);
        setError("An error occurred while fetching auth status.");
      }
    };

    fetchCsrf();
    fetchAuthStatus();
  }, []);

  return (
    <main className="min-h-screen bg-gradient-to-br from-purple-950 to-indigo-950 flex flex-col items-center justify-center text-white">
      {login ? (
        <div className="bg-[#f5f5f5]/10 backdrop-blur-md rounded-lg shadow-2xl p-8 max-w-md w-full">
          <div className="flex justify-center mb-8">
            <header className="text-4xl font-bold text-[#f5f5f5]">
              Authorization
            </header>
          </div>
          {authError && (
            <div className="mb-4 p-3 bg-red-500/80 text-[#f5f5f5] rounded-lg text-center">
              <p>{error}</p>
            </div>
          )}
          <div className="space-y-6">
            <div className="flex flex-col space-y-2">
              <label className="text-lg font-medium">Username:</label>
              <input
                className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-[#f5f5f5]/60"
                type="text"
                name="username"
                placeholder="Enter username"
                onChange={(event) => setUsername(event.target.value)}
              />
              {validationErrors.username && (
                <p className="text-red-500 text-sm">{validationErrors.username}</p>
              )}
            </div>
            <div className="flex flex-col space-y-2">
              <label className="text-lg font-medium">Password:</label>
              <input
                className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-[#f5f5f5]/60"
                type="password"
                name="password"
                placeholder="Enter password"
                onChange={(event) => setPassword(event.target.value)}
              />
              {validationErrors.password && (
                <p className="text-red-500 text-sm">{validationErrors.password}</p>
              )}
            </div>
            <div className="flex items-center space-x-2 px-1">
              <input
                className="w-5 h-5 rounded-md focus:ring-2 focus:ring-[#f5f5f5]/60 border border-gray-300/50 bg-white/20 checked:bg-purple-600 checked:border-purple-600"
                type="checkbox"
                name="remember-me"
                id="remember-me"
                onChange={(event) => setRememberMe(event.target.checked)}
              />
              <label className="text-[#f5f5f5]/50 text-[17px]">
                Remember me
              </label>
            </div>
            <div className="flex space-x-4">
              <button
                onClick={signin}
                className="w-full bg-purple-800 hover:bg-purple-900 text-[#f5f5f5] font-semibold py-3 rounded-lg transition duration-200"
              >
                Sing in
              </button>
              <button
                onClick={signup}
                className="w-full bg-indigo-800 hover:bg-indigo-900 text-[#f5f5f5] font-semibold py-3 rounded-lg transition duration-200"
              >
                Sing up
              </button>
            </div>
          </div>
        </div>
      ) : (
        <div className="bg-white/10 backdrop-blur-md rounded-lg shadow-2xl p-8 max-w-md w-full text-center">
          <p className="text-2xl mb-6">{data}</p>
          <button
            onClick={logout}
            className="w-full bg-red-500/80 hover:bg-red-600/80 text-white font-semibold py-3 rounded-lg transition duration-200"
          >
            Sing out
          </button>
        </div>
      )}
    </main>
  );
}