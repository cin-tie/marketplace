import Header from "@/app/components/Header";
import Foother from "@/app/components/Foother";

export default function Layout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
        <Header />
        {children}
        <Foother />
    </>
  );
}
