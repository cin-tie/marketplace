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
  // Authentication states
  const [data, setData] = useState("");
  const [login, setLogin] = useState(false);
  const [isSignUp, setIsSignUp] = useState(false);
  const [authError, setAuthError] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  // Form states
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [rememberMe, setRememberMe] = useState(false);
  const [csrf, setCsrf] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  // Password reset states
  const [showForgotPassword, setShowForgotPassword] = useState(false);
  const [resetEmail, setResetEmail] = useState("");
  const [resetCode, setResetCode] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [resetStep, setResetStep] = useState(1); // 1: email, 2: code, 3: new password
  const [canResendCode, setCanResendCode] = useState(true);
  const [resendTimer, setResendTimer] = useState(0);

  // Product states
  interface Product {
    id: string;
    name: string;
    price: number;
  }
  const [products, setProducts] = useState<Product[]>([]);
  const [showAddProductForm, setShowAddProductForm] = useState(false);
  const [newProduct, setNewProduct] = useState({
    name: "",
    price: ""
  });

  // Validation states
  const [validationErrors, setValidationErrors] = useState({
    username: "",
    password: "",
    email: "",
    resetEmail: "",
    resetCode: "",
    newPassword: ""
  });

  // Validation schema
  const signinSchema = z.object({
    usernameOrEmail: z.string()
      .min(1, "Username or email is required"),
    password: z.string()
      .min(8, "Password must be at least 8 characters")
      .max(256, "Password must be less than 256 characters")
      .regex(/[a-z]/, "Password must contain a lowercase letter")
      .regex(/[A-Z]/, "Password must contain an uppercase letter")
      .regex(/[0-9]/, "Password must contain a number")
  });
  const signupSchema = z.object({
    username: z
      .string()
      .min(6, "Username must be at least 6 characters")
      .max(256, "Username must be less than 256 characters")
      .regex(
        /^[a-zA-Z0-9_]+$/,
        "Username can only contain letters, numbers and underscores",
      ),
    email: z
      .string()
      .email("Please enter a valid email address"),
    password: z
      .string()
      .min(8, "Password must be at least 8 characters")
      .max(256, "Password must be less than 256 characters")
      .regex(/[a-z]/, "Password must contain a lowercase letter")
      .regex(/[A-Z]/, "Password must contain an uppercase letter")
      .regex(/[0-9]/, "Password must contain a number"),
  });

  const resetPasswordSchema = z.object({
    resetEmail: z.string().email("Please enter a valid email address"),
    resetCode: z.string().length(6, "Code must be 6 digits"),
    newPassword: z
      .string()
      .min(8, "Password must be at least 8 characters")
      .max(256, "Password must be less than 256 characters")
      .regex(/[a-z]/, "Password must contain a lowercase letter")
      .regex(/[A-Z]/, "Password must contain an uppercase letter")
      .regex(/[0-9]/, "Password must contain a number"),
  });
  
  // Create partial schemas for each step
  const resetStepSchemas = {
    1: resetPasswordSchema.pick({ resetEmail: true }),
    2: resetPasswordSchema.pick({ resetCode: true }),
    3: resetPasswordSchema.pick({ newPassword: true }),
  };

  // Timer for resend code functionality
  useEffect(() => {
    let interval: NodeJS.Timeout;
    
    if (resendTimer > 0) {
      interval = setInterval(() => {
        setResendTimer(prev => prev - 1);
      }, 1000);
    } else if (resendTimer === 0 && !canResendCode) {
      setCanResendCode(true);
    }
    
    return () => clearInterval(interval);
  }, [resendTimer, canResendCode]);

  // Validate inputs based on current form
  const validateInputs = () => {
    try {
      if (showForgotPassword) {
        // Validate based on current step
        const currentSchema = resetStepSchemas[resetStep as keyof typeof resetStepSchemas];
        const validationData = {
          resetEmail: resetStep === 1 ? resetEmail : undefined,
          resetCode: resetStep === 2 ? resetCode : undefined,
          newPassword: resetStep === 3 ? newPassword : undefined,
        };
        
        currentSchema.parse(validationData);
        
        // Clear relevant errors
        setValidationErrors(prev => ({
          ...prev,
          resetEmail: resetStep === 1 ? "" : prev.resetEmail,
          resetCode: resetStep === 2 ? "" : prev.resetCode,
          newPassword: resetStep === 3 ? "" : prev.newPassword,
        }));
        
        return true;
      }
  
      // Rest of your existing validation for normal auth flow
      if(isSignUp){
        signupSchema.parse({username, email, password});
        setValidationErrors({
          ...validationErrors,
          username: "",
          email: "",
          password: ""
        });
      }
      else{
        signinSchema.parse({ usernameOrEmail: username, password });
        setValidationErrors({
          ...validationErrors,
          username: "",
          password: ""
        });
      }
      return true;
    } catch (err) {
      if (err instanceof z.ZodError) {
        const errors = err.errors.reduce((acc, curr) => {
          const path = curr.path[0];
          return { ...acc, [path]: curr.message };
        }, {} as Record<string, string>);
        
        setValidationErrors(prev => ({
          ...prev,
          ...errors
        }));
      }
      return false;
    }
  };

  // Password reset functions
  const initiatePasswordReset = async () => {
    if (!validateInputs()) return;
  
    try {
      // 1. Get fresh CSRF token
      const csrfResp = await fetch("http://localhost:8080/auth/csrf", {
        credentials: "include"
      });
      
      if (!csrfResp.ok) throw new Error("Failed to get CSRF token");
      
      const { token } = await csrfResp.json();
      setCsrf(token);
  
      // 2. Make the reset request with proper JSON body
      const response = await fetch("http://localhost:8080/auth/password-reset/initiate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": token,
        },
        body: JSON.stringify({ email: resetEmail }),
        credentials: "include",
      });
  
      // 3. Handle response
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Request failed");
      }
  
      const result = await response.text();
      setResetStep(2);
      setSuccessMessage(result || "Reset code sent successfully");
      setError("");
      setCanResendCode(false);
      setResendTimer(60); // Start 1-minute cooldown
      
    } catch (err) {
      console.error("Password reset error:", err);
      setError(err instanceof Error ? err.message : "Failed to initiate reset");
      setSuccessMessage("");
    }
  };

  const handleResendCode = async () => {
    if (!canResendCode) return;
    
    try {
      const response = await fetch("http://localhost:8080/auth/password-reset/initiate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": csrf,
        },
        body: JSON.stringify({ email: resetEmail }),
        credentials: "include",
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to resend code");
      }

      setCanResendCode(false);
      setResendTimer(60); // 1 minute cooldown
      setSuccessMessage("Reset code resent successfully");
      setError("");
      
    } catch (err) {
      console.error("Resend code error:", err);
      setError(err instanceof Error ? err.message : "Failed to resend code");
    }
  };

  const validateResetCode = async () => {
    if (!validateInputs()) return;

    try {
      const response = await fetch("http://localhost:8080/auth/password-reset/validate-code", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": csrf,
        },
        body: JSON.stringify({ 
          email: resetEmail,
          code: resetCode 
        }),
        credentials: "include",
      });
      
      if (response.ok) {
        setResetStep(3);
        setAuthError(false);
        setError("");
        setSuccessMessage("Code verified. Please enter your new password");
      } else {
        const result = await response.json();
        throw new Error(result.message || "Invalid reset code");
      }
    } catch (err) {
      setAuthError(true);
      setError(err instanceof Error ? err.message : "Failed to validate reset code");
      setSuccessMessage("");
    }
  };

  const completePasswordReset = async () => {
    if (!validateInputs()) return;

    try {
      const response = await fetch("http://localhost:8080/auth/password-reset/complete", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": csrf,
        },
        body: JSON.stringify({ 
          email: resetEmail,
          code: resetCode,
          newPassword: newPassword 
        }),
        credentials: "include",
      });
      
      if (response.ok) {
        setShowForgotPassword(false);
        setResetStep(1);
        setResetEmail("");
        setResetCode("");
        setNewPassword("");
        setAuthError(false);
        setError("");
        setSuccessMessage("Password reset successfully. You can now sign in.");
      } else {
        const result = await response.json();
        throw new Error(result.message || "Failed to reset password");
      }
    } catch (err) {
      setAuthError(true);
      setError(err instanceof Error ? err.message : "Failed to complete password reset");
      setSuccessMessage("");
    }
  };
  // Authentication functions (existing)
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
          usernameOrEmail: username,
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
        setSuccessMessage("");
      }
    } catch (err) {
      console.error("Sign in error:", err);
      setAuthError(true);
      setError("An error occurred while signing in");
      setSuccessMessage("");
    }
  };

  const signup = async () => {
    if (!validateInputs()) return;

    try {
      const csrfResp = await fetch("http://localhost:8080/auth/csrf", {
        credentials: "include"
      });
      const { token } = await csrfResp.json();
      const response = await fetch("http://localhost:8080/auth/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": token,
        },
        body: JSON.stringify({
          username: username,
          email: email,
          password: password,
          rememberme: rememberMe,
        }),
        credentials: "include",
      });
      const result = await response.json();
      
      if (result.success) {
        setAuthError(false);
        setError("");
        setSuccessMessage("Registration successful! Please check your email to verify your account.");
        setUsername("");
        setPassword("");
        setEmail("");
      } else {
        setAuthError(true);
        setError(result.errors[0]);
        setSuccessMessage("");
      }
    } catch (err) {
      console.error("Sign up error:", err);
      setAuthError(true);
      setError("An error occurred while signing up");
      setSuccessMessage("");
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
      console.error("Logout error:", err);
      setAuthError(true);
      setError("An error occurred during logout");
    }
  };

  // Product functions (existing)
  const fetchProducts = async () => {
    try {
      const response = await fetch("http://localhost:8080/products/my", {
        credentials: "include",
      });
      const data = await response.json();
      setProducts(data);
    } catch (err) {
      console.error("Error fetching products:", err);
      setError("Failed to load products");
    }
  };

  const handleAddProduct = async () => {
    try {
      const response = await fetch("http://localhost:8080/products", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-CSRF-TOKEN": csrf,
        },
        body: JSON.stringify(newProduct),
        credentials: "include",
      });
      
      if (response.ok) {
        setShowAddProductForm(false);
        setNewProduct({ name: "", price: "" });
        fetchProducts();
      } else {
        const result = await response.json();
        setError(result.errors || "Failed to add product");
      }
    } catch (err) {
      console.error("Error adding product:", err);
      setError("An error occurred while adding product");
    }
  };
  
  const handleDeleteProduct = async (id: string) => {
    try {
      const response = await fetch(`http://localhost:8080/products/${id}`, {
        method: "DELETE",
        headers: {
          "X-CSRF-TOKEN": csrf,
        },
        credentials: "include",
      });
      
      if (response.ok) {
        fetchProducts();
      } else {
        setError("Failed to delete product");
      }
    } catch (err) {
      console.error("Error deleting product:", err);
      setError("An error occurred while deleting product");
    }
  };

  // Data loading functions (existing)
  const loadData = async () => {
    try {
      const response = await fetch("http://localhost:8080/test", {
        credentials: "include",
      });
      const data = await response.text();
      setData(data);
    } catch (err) {
      console.error("Error loading data:", err);
      setAuthError(true);
      setError("An error occurred while loading data");
    }
  };

  // Initialization effect (existing)
  useEffect(() => {
    const fetchCsrf = async () => {
      try {
        const response = await fetch("http://localhost:8080/auth/csrf", {
          credentials: "include",
        });
        const data = await response.json();
        setCsrf(data.token);
      } catch (err) {
        console.error("Error fetching CSRF token:", err);
        setAuthError(true);
        setError("An error occurred while fetching CSRF token");
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
          fetchProducts();
        } else {
          setLogin(true);
        }
      } catch (err) {
        console.error("Error fetching auth status:", err);
        setAuthError(true);
        setError("An error occurred while fetching auth status");
      }
    };

    fetchCsrf();
    fetchAuthStatus();
  }, []);

  return (
    <main className="min-h-screen bg-gradient-to-br from-purple-950 to-indigo-950 flex flex-col items-center justify-center text-white p-4">
      {login ? (
        showForgotPassword ? (
          // Password reset flow
          <div className="bg-white/10 backdrop-blur-md rounded-lg shadow-2xl p-8 max-w-md w-full">
            <div className="flex justify-center mb-8">
              <h1 className="text-3xl font-bold text-[#f5f5f5]">
                {resetStep === 1 ? "Reset Password" : 
                 resetStep === 2 ? "Enter Reset Code" : 
                 "New Password"}
              </h1>
            </div>
            
            {successMessage && (
              <div className="mb-4 p-3 bg-green-500/80 text-[#f5f5f5] rounded-lg text-center">
                <p>{successMessage}</p>
              </div>
            )}
            
            {authError && (
              <div className="mb-4 p-3 bg-red-500/80 text-[#f5f5f5] rounded-lg text-center">
                <p>{error}</p>
              </div>
            )}
            
            <div className="space-y-6">
              {resetStep === 1 && (
                <div className="flex flex-col space-y-2">
                  <label className="text-lg font-medium">Email:</label>
                  <input
                    className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-purple-500/60"
                    type="email"
                    value={resetEmail}
                    placeholder="Enter your email"
                    onChange={(e) => setResetEmail(e.target.value)}
                  />
                  {validationErrors.resetEmail && (
                    <p className="text-red-500 text-sm">{validationErrors.resetEmail}</p>
                  )}
                </div>
              )}
              
              {resetStep === 2 && (
                <>
                  <div className="flex flex-col space-y-2">
                    <label className="text-lg font-medium">Reset Code:</label>
                    <input
                      className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-purple-500/60"
                      type="text"
                      value={resetCode}
                      placeholder="Enter 6-digit code"
                      onChange={(e) => setResetCode(e.target.value)}
                    />
                    {validationErrors.resetCode && (
                      <p className="text-red-500 text-sm">{validationErrors.resetCode}</p>
                    )}
                  </div>
                  
                  <div className="text-center">
                    <button
                      onClick={handleResendCode}
                      disabled={!canResendCode}
                      className={`text-[#f5f5f5]/70 hover:text-[#f5f5f5] underline transition duration-200 ${
                        !canResendCode ? "opacity-50 cursor-not-allowed" : ""
                      }`}
                    >
                      {canResendCode ? "Resend Code" : `Resend available in ${resendTimer}s`}
                    </button>
                  </div>
                </>
              )}
              
              {resetStep === 3 && (
                <div className="flex flex-col space-y-2 relative">
                  <label className="text-lg font-medium">New Password:</label>
                  <div className="relative">
                    <input
                      className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-purple-500/60 pr-10"
                      type={showPassword ? "text" : "password"}
                      value={newPassword}
                      placeholder="Enter new password"
                      onChange={(e) => setNewPassword(e.target.value)}
                    />
                    <button
                      type="button"
                      className="absolute right-3 top-1/2 transform -translate-y-1/2 text-[#f5f5f5]/70 hover:text-[#f5f5f5]"
                      onClick={() => setShowPassword(!showPassword)}
                    >
                      {showPassword ? (
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                        </svg>
                      ) : (
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                      )}
                    </button>
                  </div>
                  {validationErrors.newPassword && (
                    <p className="text-red-500 text-sm">{validationErrors.newPassword}</p>
                  )}
                </div>
              )}
              
              <div className="flex space-x-4">
                <button
                  onClick={() => {
                    if (resetStep === 1) initiatePasswordReset();
                    else if (resetStep === 2) validateResetCode();
                    else if (resetStep === 3) completePasswordReset();
                  }}
                  className="w-full bg-purple-600 hover:bg-purple-700 text-white font-semibold py-3 rounded-lg transition duration-200"
                >
                  {resetStep === 1 ? "Send Reset Code" : 
                   resetStep === 2 ? "Verify Code" :
                   "Reset Password"}
                </button>
              </div>
              
              <div className="text-center">
                <button
                  onClick={() => {
                    setShowForgotPassword(false);
                    setResetStep(1);
                    setResetEmail("");
                    setResetCode("");
                    setNewPassword("");
                    setAuthError(false);
                    setError("");
                    setSuccessMessage("");
                  }}
                  className="text-[#f5f5f5]/70 hover:text-[#f5f5f5] underline transition duration-200"
                >
                  Back to Sign In
                </button>
              </div>
            </div>
          </div>
        ) : (
          // Normal auth flow
          <div className="bg-white/10 backdrop-blur-md rounded-lg shadow-2xl p-8 max-w-md w-full">
            <div className="flex justify-center mb-8">
              <h1 className="text-3xl font-bold text-[#f5f5f5]">
                {isSignUp ? "Create Account" : "Welcome Back"}
              </h1>
            </div>
            
            {successMessage && (
              <div className="mb-4 p-3 bg-green-500/80 text-[#f5f5f5] rounded-lg text-center">
                <p>{successMessage}</p>
              </div>
            )}
            
            {authError && (
              <div className="mb-4 p-3 bg-red-500/80 text-[#f5f5f5] rounded-lg text-center">
                <p>{error}</p>
              </div>
            )}
            
            <div className="space-y-6">
              {isSignUp ? (
                <>
                <div className="flex flex-col space-y-2">
                  <label className="text-lg font-medium">Email:</label>
                  <input
                    className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-purple-500/60"
                    type="email"
                    value={email}
                    placeholder="Enter your email"
                    onChange={(e) => setEmail(e.target.value)}
                  />
                  {validationErrors.email && (
                    <p className="text-red-500 text-sm">{validationErrors.email}</p>
                  )}
                </div>
                <div className="flex flex-col space-y-2">
                  <label className="text-lg font-medium">Username:</label>
                  <input
                    className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-purple-500/60"
                    type="text"
                    value={username}
                    placeholder="Enter your username"
                    onChange={(e) => setUsername(e.target.value)}
                  />
                  {validationErrors.username && (
                    <p className="text-red-500 text-sm">{validationErrors.username}</p>
                  )}
              </div>
                </>
              ):(
                <div className="flex flex-col space-y-2">
                <label className="text-lg font-medium">Username or email:</label>
                <input
                  className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-purple-500/60"
                  type="text"
                  value={username}
                  placeholder="Enter your username or email"
                  onChange={(e) => setUsername(e.target.value)}
                />
                {validationErrors.username && (
                  <p className="text-red-500 text-sm">{validationErrors.username}</p>
                )}
              </div>
              )}
              
              
              <div className="flex flex-col space-y-2 relative">
                <label className="text-lg font-medium">Password:</label>
                <div className="relative">
                  <input
                    className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-purple-500/60 pr-10"
                    type={showPassword ? "text" : "password"}
                    value={password}
                    placeholder="Enter your password"
                    onChange={(e) => setPassword(e.target.value)}
                  />
                  <button
                    type="button"
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-[#f5f5f5]/70 hover:text-[#f5f5f5]"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                      </svg>
                    ) : (
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                      </svg>
                    )}
                  </button>
                </div>
                {validationErrors.password && (
                  <p className="text-red-500 text-sm">{validationErrors.password}</p>
                )}
              </div>                                  
              {!isSignUp && (
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <input
                    className="w-5 h-5 rounded-md focus:ring-2 focus:ring-purple-500/60 border border-gray-300/50 bg-white/20 checked:bg-purple-600 checked:border-purple-600"
                    type="checkbox"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                  />
                  <label className="text-[#f5f5f5]/70 text-[17px]">
                    Remember me
                  </label>
                </div>
                
                <button
                onClick={() => {
                  setShowForgotPassword(true);
                    setIsSignUp(false);
                    setAuthError(false);
                    setError("");
                    setSuccessMessage("");
                  }}
                  className="text-[#f5f5f5]/70 hover:text-[#f5f5f5] underline text-sm transition duration-200"
                >
                  Forgot password?
                </button>
              </div>
              )}
              
              <button
                onClick={isSignUp ? signup : signin}
                className={`w-full ${
                  isSignUp 
                    ? "bg-purple-600 hover:bg-purple-700" 
                    : "bg-purple-600 hover:bg-purple-700"
                } text-white font-semibold py-3 rounded-lg transition duration-200`}
              >
                {isSignUp ? "Sign Up" : "Sign In"}
              </button>
              
              <div className="text-center">
                <button
                  onClick={() => {
                    setIsSignUp(!isSignUp);
                    setAuthError(false);
                    setError("");
                    setSuccessMessage("");
                  }}
                  className="text-[#f5f5f5]/70 hover:text-[#f5f5f5] underline transition duration-200"
                >
                  {isSignUp 
                    ? "Already have an account? Sign In" 
                    : "Don't have an account? Sign Up"}
                </button>
              </div>
            </div>
          </div>
        )
      ) : (
        // Dashboard (existing)
        <div className="bg-white/10 backdrop-blur-md rounded-lg shadow-2xl p-8 max-w-4xl w-full">
          {/* User dashboard header */}
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold text-[#f5f5f5]">Your Dashboard</h1>
            <button
              onClick={logout}
              className="bg-purple-600 hover:bg-purple-700 text-white font-semibold py-2 px-4 rounded-lg transition duration-200"
            >
              Sign Out
            </button>
          </div>
          
          {/* Welcome message */}
          <div className="text-center mb-8">
            <p className="text-2xl text-purple-300">{data || "Welcome to your account"}</p>
          </div>
          
          {/* Products section */}
          <div className="mt-8">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold">My Products</h2>
              <button
                onClick={() => setShowAddProductForm(true)}
                className="bg-green-600 hover:bg-green-700 text-white font-semibold py-2 px-4 rounded-lg transition duration-200"
              >
                Add Product
              </button>
            </div>
            
            {/* Products grid */}
            {products.length > 0 ? (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {products.map((product) => (
                  <div key={product.id} className="bg-white/10 p-4 rounded-lg shadow-md hover:shadow-lg transition duration-200">
                    <h3 className="text-xl font-semibold mb-2">{product.name}</h3>
                    <p className="text-purple-400 text-lg mb-4">${product.price.toFixed(2)}</p>
                    <button
                      onClick={() => handleDeleteProduct(product.id)}
                      className="w-full bg-red-500/80 hover:bg-red-600/80 text-white font-semibold py-1 px-2 rounded-lg transition duration-200"
                    >
                      Delete
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-8">
                <p className="text-gray-400 text-lg">No products found. Add your first product!</p>
              </div>
            )}
          </div>
        </div>
      )}
      
      {/* Add Product Modal (existing) */}
      {showAddProductForm && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center p-4 z-50">
          <div className="bg-[#1e1b4b]/90 backdrop-blur-md rounded-lg shadow-2xl p-8 max-w-md w-full">
            <h2 className="text-2xl font-bold mb-6">Add New Product</h2>
            
            {error && (
              <div className="mb-4 p-3 bg-red-500/80 text-[#f5f5f5] rounded-lg">
                <p>{error}</p>
              </div>
            )}
            
            <div className="space-y-4">
              <div className="flex flex-col space-y-2">
                <label className="text-lg font-medium">Product Name:</label>
                <input
                  type="text"
                  placeholder="Enter product name"
                  value={newProduct.name}
                  onChange={(e) => setNewProduct({...newProduct, name: e.target.value})}
                  className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-purple-500/60"
                />
              </div>
              
              <div className="flex flex-col space-y-2">
                <label className="text-lg font-medium">Price:</label>
                <input
                  type="number"
                  placeholder="Enter price"
                  value={newProduct.price}
                  onChange={(e) => setNewProduct({...newProduct, price: e.target.value})}
                  className="w-full p-3 rounded-lg bg-[#f5f5f5]/20 placeholder-[#f5f5f5]/50 text-[#f5f5f5] focus:outline-none focus:ring-2 focus:ring-purple-500/60"
                  min="0"
                  step="0.01"
                />
              </div>
              
              <div className="flex space-x-4 pt-4">
                <button
                  onClick={handleAddProduct}
                  className="w-full bg-purple-600 hover:bg-purple-700 text-white font-semibold py-3 rounded-lg transition duration-200"
                >
                  Add Product
                </button>
                <button
                  onClick={() => {
                    setShowAddProductForm(false);
                    setNewProduct({ name: "", price: "" });
                    setError("");
                  }}
                  className="w-full bg-gray-500 hover:bg-gray-600 text-white font-semibold py-3 rounded-lg transition duration-200"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </main>
  );
}