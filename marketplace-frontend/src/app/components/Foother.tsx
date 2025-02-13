"use client";

import { usePathname } from "next/navigation";
import Link from "next/link";
import { useEffect, useState } from "react";
import { Home, Search, MessageSquare, User } from "lucide-react";

const navItems = [
  { name: "Home", href: "/home", icon: Home },
  { name: "Search", href: "/search", icon: Search },
  { name: "Messages", href: "/message", icon: MessageSquare },
  { name: "Profile", href: "/profile", icon: User },
];

export default function Footer() {
  const pathname = usePathname();
  const [activeIndex, setActiveIndex] = useState(-1);

  useEffect(() => {
    const activeIndex = navItems.findIndex((item) => item.href === pathname);
    setActiveIndex(activeIndex);
    console.log(`pathname: ${pathname}, active index: ${activeIndex}`);
  }, [pathname]);

  return (
    <footer className="fixed bottom-0 w-full bg-[#534487] p-3 shadow-md">
      <nav className="relative flex justify-around text-[#534487]">
        {navItems.map((item, index) => {
          const Icon = item.icon;
          const isActive = activeIndex === index;
          return (
            <Link
              key={item.href}
              href={item.href}
              className="flex flex-col items-center w-1/4 relative z-10"
            >
              <div
                className={`absolute bottom-2 w-10 h-10 bg-[#534487] rounded-full transition-all duration-500 transform ${
                  isActive
                    ? "scale-[220%] translate-y-[-20%] opacity-100"
                    : " opacity-0"
                }`}
              />
              <Icon
                size={24}
                className={`relative z-10 transition-all duration-500 ${
                  isActive
                    ? "scale-[150%] text-[#bebebe] animation: translate-y-[-50%]"
                    : "text-[#f5f5f5]"
                }`}
              />
              <span
                className={`text-xs z-10 transition-all duration-500 text-[#bebebe]
                } transform ${isActive ? "scale-0" : "scale-100"}`}
              >
                {item.name}
              </span>
            </Link>
          );
        })}
      </nav>
    </footer>
  );
}
