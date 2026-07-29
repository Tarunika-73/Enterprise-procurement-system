import './LandingPage.css';
import Navbar     from '../../components/Landing/Navbar/Navbar';
import Hero       from '../../components/Landing/Hero/Hero';
import About      from '../../components/Landing/About/About';
import Statistics from '../../components/Landing/Statistics/Statistics';
import Features   from '../../components/Landing/Features/Features';
import Contact    from '../../components/Landing/Contact/Contact';
import Footer     from '../../components/Landing/Footer/Footer';

const LandingPage = () => (
  <>
    <Navbar />
    <Hero />
    <About />
    <Statistics />
    <Features />
    <Contact />
    <Footer />
  </>
);

export default LandingPage;
