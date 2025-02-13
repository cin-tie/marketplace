"use client";

import { useEffect, useState } from "react";
import Image from "next/image";
import "tailwindcss/tailwind.css";
import "@/styles/globals.css";

export default function Home() {
  const [hasLoaded, setHasLoaded] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setHasLoaded(true);
    }, 200);

    return () => clearTimeout(timer);
  }, []);
  return (
    <div className="relative h-screen">
      <div
        className={`absolute inset-0 bg-[url('/images/background-homepage.png')] bg-cover bg-center transition-opacity duration-1000 ${hasLoaded ? "opacity-20" : "opacity-0"}`}
      ></div>

      <div className="relative flex flex-col justify-center items-center h-full">
        <div
          className={`flex justify-center mt-[-100px] ${hasLoaded ? "animate-fadeInUp" : "opacity-0"}`}
        >
          <h1 className="text-4xl font-bold text-[#f5f5f5]">WELCOME</h1>
        </div>

        <div
          className={`flex justify-center mt-10 ${hasLoaded ? "animate-fadeInUp" : "opacity-0"}`}
        >
          <h1 className="text-7xl font-bold text-[#534487]">MarketPlace</h1>
        </div>

        <div className="flex relative">
          <div
            className={`z-0 w-auto ${hasLoaded ? "animate-slideInUp" : "opacity-0"}`}
          >
            <Image
              src="/images/pic1.png"
              width={300}
              height={0}
              alt="Picture 1"
              style={{ height: "auto" }}
            />
          </div>

          <div
            className={`absolute z-10 ${hasLoaded ? "animate-slideInRight" : "opacity-0"}`}
          >
            <Image
              src="/images/pic2.png"
              width={250}
              height={0}
              alt="Picture 2"
              style={{ height: "auto" }}
            />
          </div>

          <div
            className={`absolute z-5 ${hasLoaded ? "animate-slideInLeft" : "opacity-0"}`}
          >
            <Image
              src="/images/pic3.png"
              width={220}
              height={0}
              alt="Picture 3"
              style={{ height: "auto" }}
            />
          </div>
        </div>

        <div
          className={`flex justify-center mt-[250px] ${hasLoaded ? "animate-fadeInUp" : "opacity-0"}`}
        >
          <h1 className="text-3xl text-center font-bold text-[#f5f5f5]">
            A platform for quickly selling and purchasing goods
          </h1>
        </div>
      </div>
    </div>
  );
}
