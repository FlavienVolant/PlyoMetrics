package com.example.plyometrics.analysis

import com.example.plyometrics.model.Acceleration
import com.example.plyometrics.model.Rotation
import com.example.plyometrics.model.SensorPoint
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test

class JumpDetectorTest {

    private lateinit var detector: JumpDetector

    @Before
    fun setUp() {
        detector = JumpDetector()
    }

    private fun point(time: Long, magnitude: Double): SensorPoint =
        SensorPoint(time, Acceleration(0f, 0f, magnitude.toFloat()), Rotation(0, 0, 0, 0))

    @Test
    fun `findImpulse returns first peak`() {

        val session = listOf(
            point(0, 9.8),
            point(20, 10.0),
            point(40, 18.0),
            point(60, 23.0),
            point(80, 15.0)
        )

        val impulse = detector.findImpulse(session)

        assertNotNull(impulse)
        assertEquals(60L, impulse!!.timestamp)
    }

    @Test
    fun `findImpulse returns null when no peak exists`() {

        val session = listOf(
            point(0, 9.8),
            point(20, 10.0),
            point(40, 10.2),
            point(60, 9.9)
        )

        assertNull(detector.findImpulse(session))
    }

    @Test
    fun `findTakeOff returns first free fall point`() {

        val session = listOf(
            point(0, 9.8),
            point(20, 20.0),   // impulsion
            point(40, 15.0),
            point(60, 2.0),    // lift off
            point(80, 0.5)
        )

        val impulse = detector.findImpulse(session)!!

        val takeOff = detector.findTakeOff(session, impulse)

        assertEquals(60L, takeOff!!.timestamp)
    }

    @Test
    fun `findLanding returns first landing peak`() {

        val session = listOf(
            point(0, 9.8),
            point(20, 20.0),
            point(40, 2.0),
            point(60, 0.5),
            point(80, 1.0),
            point(100, 18.0),
            point(120, 24.0)
        )

        val takeOff = detector.findTakeOff(
                session,
                detector.findImpulse(session)!!
            )!!

        val landing = detector.findLanding(session, takeOff)

        assertEquals(120L, landing!!.timestamp)
    }

    @Test
    fun `analyze returns jump result`() {

        val session = listOf(
            point(0, 9.8),
            point(20, 20.0),
            point(40, 15.0),
            point(60, 2.0),
            point(80, 0.5),
            point(100, 0.8),
            point(120, 18.0),
            point(140, 24.0)
        )

        val jump = detector.analyze(session)

        assertNotNull(jump)

        assertEquals(60L, jump!!.takeOffTime)
        assertEquals(140L, jump.landingTime)
        assertEquals(80L, jump.flightTime)
    }

    @Test
    fun `detect jump from realistic sensor data`() {
        val jumpSession = listOf(
            point(925, 8.869524),
            point(930, 9.2237425),
            point(935, 9.451605),
            point(940, 9.797148),
            point(945, 9.91398),
            point(950, 10.093536),
            point(955, 10.23015),
            point(960, 10.466364),
            point(965, 10.569624),
            point(970, 10.325463),
            point(975, 9.7048645),
            point(980, 9.012264),
            point(985, 8.523885),
            point(990, 8.319611),
            point(995, 8.574761),
            point(1000, 8.922434),
            point(1005, 9.250234),
            point(1010, 9.547485),
            point(1015, 9.95233),
            point(1020, 10.554519),
            point(1025, 11.215307),
            point(1030, 11.830938),
            point(1035, 12.162391),
            point(1040, 12.404479),
            point(1045, 12.591389),
            point(1050, 13.1605425),
            point(1055, 14.338943),
            point(1060, 15.637175),
            point(1065, 17.338612),
            point(1070, 18.994045),
            point(1075, 19.374811),
            point(1080, 17.768545),
            point(1085, 15.1059065),
            point(1090, 12.4749775),
            point(1095, 10.10595),
            point(1100, 8.6751175),
            point(1105, 7.8005195),
            point(1110, 8.24597),
            point(1115, 9.109447),
            point(1120, 10.283662),
            point(1125, 10.947283),
            point(1130, 11.061319),
            point(1135, 10.845458),
            point(1140, 10.277417),
            point(1145, 9.846019),
            point(1150, 9.115213),
            point(1155, 8.8693905),
            point(1160, 9.453953),
            point(1165, 10.275253),
            point(1170, 10.907987),
            point(1175, 11.399687),
            point(1180, 11.686956),
            point(1185, 11.800169),
            point(1190, 10.491474),
            point(1195, 10.358816),
            point(1200, 10.986743),
            point(1205, 11.458445),
            point(1210, 12.288877),
            point(1215, 12.501328),
            point(1220, 12.191264),
            point(1225, 11.90986),
            point(1230, 11.778562),
            point(1235, 11.942317),
            point(1240, 12.101041),
            point(1245, 12.101151),
            point(1250, 11.832255),
            point(1255, 11.544021),
            point(1260, 11.3028755),
            point(1265, 11.09516),
            point(1270, 10.678672),
            point(1275, 10.522026),
            point(1280, 10.632275),
            point(1285, 10.94044),
            point(1290, 11.596623),
            point(1295, 11.803343),
            point(1300, 11.59391),
            point(1305, 11.820659),
            point(1310, 12.736567),
            point(1315, 14.173484),
            point(1320, 16.049162),
            point(1325, 17.362038),
            point(1330, 18.971157),
            point(1335, 21.130735),
            point(1340, 23.846813),
            point(1345, 27.173893),
            point(1350, 33.255688),
            point(1355, 42.07127),
            point(1360, 52.24815),
            point(1365, 59.89022),
            point(1370, 64.457245),
            point(1375, 66.00307),
            point(1380, 64.96245),
            point(1385, 62.685566),
            point(1390, 60.566044),
            point(1395, 58.46205),
            point(1400, 55.754967),
            point(1405, 53.96364),
            point(1410, 47.608418),
            point(1415, 40.625404),
            point(1420, 36.70668),
            point(1425, 30.647263),
            point(1430, 25.911465),
            point(1435, 22.859774),
            point(1440, 20.27354),
            point(1445, 17.855946),
            point(1450, 15.3419695),
            point(1455, 13.202805),
            point(1460, 12.50815),
            point(1465, 12.618815),
            point(1470, 12.429238),
            point(1475, 11.961892),
            point(1480, 11.267007),
            point(1485, 10.232091),
            point(1490, 9.075949),
            point(1495, 8.219143),
            point(1500, 7.9633036),
            point(1505, 8.497569),
            point(1510, 9.104803),
            point(1515, 9.781128),
            point(1520, 10.693113),
            point(1525, 11.614239),
            point(1530, 11.999834),
            point(1535, 11.621643),
            point(1540, 10.5144005),
            point(1545, 10.566777),
            point(1550, 10.571532),
            point(1555, 11.272267),
            point(1560, 12.381374),
            point(1565, 13.459615),
            point(1570, 14.2902155),
            point(1575, 15.127063),
            point(1580, 15.367655),
            point(1585, 14.991707),
            point(1590, 14.30531),
            point(1595, 13.553348),
            point(1600, 13.070422),
            point(1605, 12.849737),
            point(1610, 13.169743),
            point(1615, 13.690902),
            point(1620, 14.136563),
            point(1625, 14.051973),
            point(1630, 13.059707),
            point(1635, 11.748537),
            point(1640, 10.449196),
            point(1645, 8.529204),
            point(1650, 7.231973),
            point(1655, 6.218062),
            point(1660, 5.390398),
            point(1665, 5.3431115),
            point(1670, 5.433106),
            point(1675, 5.341575),
            point(1680, 5.2023926),
            point(1685, 4.878704),
            point(1690, 5.1476293),
            point(1695, 5.905342),
            point(1700, 6.659297),
            point(1705, 7.355),
            point(1710, 7.88776),
            point(1715, 8.52154),
            point(1720, 9.0630245),
            point(1725, 9.405794),
            point(1730, 9.701007),
            point(1735, 9.510386),
            point(1740, 9.149184),
            point(1745, 9.037797),
            point(1750, 8.749559),
            point(1755, 8.582219),
            point(1760, 8.43229),
            point(1765, 8.610514),
            point(1770, 8.713042),
            point(1775, 8.5504),
            point(1780, 8.561923),
            point(1785, 8.3646755),
            point(1790, 8.250101),
            point(1795, 7.5948124),
            point(1800, 6.471002),
            point(1805, 5.3130307),
            point(1810, 4.551644),
            point(1815, 4.072407),
            point(1820, 3.9588833),
            point(1825, 3.7454622),
            point(1830, 3.7542548),
            point(1835, 3.687369),
            point(1840, 3.3699884),
            point(1845, 3.0718868),
            point(1850, 2.7301292),
            point(1855, 2.2304914),
            point(1860, 1.8514224),
            point(1865, 1.5095247),
            point(1870, 1.156615),
            point(1875, 1.0365096),
            point(1880, 1.2433631),
            point(1885, 1.6934708),
            point(1890, 2.4832938),
            point(1895, 3.1412218),
            point(1900, 3.6430407),
            point(1905, 3.9621716),
            point(1910, 4.207664),
            point(1915, 4.2711487),
            point(1920, 4.107546),
            point(1925, 3.839595),
            point(1930, 3.3844225),
            point(1935, 2.8688571),
            point(1940, 2.3495402),
            point(1945, 2.106593),
            point(1950, 2.0799317),
            point(1955, 1.9021719),
            point(1960, 1.7614557),
            point(1965, 1.5839413),
            point(1970, 1.4979057),
            point(1975, 1.4987626),
            point(1980, 1.4978138),
            point(1985, 1.4635605),
            point(1990, 1.407139),
            point(1995, 1.3661307),
            point(2000, 1.5271629),
            point(2005, 1.6692789),
            point(2010, 1.9507558),
            point(2015, 2.2775087),
            point(2020, 2.6308343),
            point(2025, 2.7623403),
            point(2030, 2.6018796),
            point(2035, 2.359279),
            point(2040, 1.228895),
            point(2045, 0.9277647),
            point(2050, 2.1170158),
            point(2055, 3.2401466),
            point(2060, 4.3237348),
            point(2065, 4.96403),
            point(2070, 5.466243),
            point(2075, 5.392261),
            point(2080, 5.2120533),
            point(2085, 4.508012),
            point(2090, 3.9738326),
            point(2095, 3.7488763),
            point(2100, 4.059493),
            point(2105, 4.5486),
            point(2110, 4.9959087),
            point(2115, 5.259237),
            point(2120, 5.6081343),
            point(2125, 5.922595),
            point(2130, 6.3805113),
            point(2135, 6.920513),
            point(2140, 7.3403955),
            point(2145, 7.7592053),
            point(2150, 8.442323),
            point(2155, 8.932964),
            point(2160, 8.955426),
            point(2165, 9.125456),
            point(2170, 9.591266),
            point(2175, 10.089547),
            point(2180, 10.607519),
            point(2185, 11.314863),
            point(2190, 12.594043),
            point(2195, 14.727603),
            point(2200, 17.384277),
            point(2205, 20.662737),
            point(2210, 24.722614),
            point(2215, 28.862568),
            point(2220, 32.467186),
            point(2225, 35.196888),
            point(2230, 36.584633),
            point(2235, 36.92968),
            point(2240, 36.271458),
            point(2245, 35.126602),
            point(2250, 34.342815),
            point(2255, 33.770885),
            point(2260, 32.98555),
            point(2265, 31.692501),
            point(2270, 30.284252),
            point(2275, 28.89426),
            point(2280, 27.977406),
            point(2285, 27.059555),
            point(2290, 25.11563),
            point(2295, 22.678982),
            point(2300, 20.474209),
            point(2305, 18.476212),
            point(2310, 16.784546),
            point(2315, 15.490464),
            point(2320, 14.787095),
            point(2325, 14.817226),
            point(2330, 15.542794),
            point(2335, 16.618805),
            point(2340, 18.12548),
            point(2345, 19.400345),
            point(2350, 20.168812),
            point(2355, 20.159563),
            point(2360, 20.05573),
            point(2365, 19.792646),
            point(2370, 19.541464),
            point(2375, 19.376099),
            point(2380, 19.19147),
            point(2385, 19.222317),
            point(2390, 19.37916),
            point(2395, 19.653349),
            point(2400, 20.154287),
            point(2405, 20.334312),
            point(2410, 20.75926),
            point(2415, 21.18395),
            point(2420, 21.574451),
            point(2425, 21.76273),
            point(2430, 21.747505),
            point(2435, 21.345451),
            point(2440, 20.566526),
            point(2445, 19.933624),
            point(2450, 19.37776),
            point(2455, 18.783806),
            point(2460, 18.296984),
            point(2465, 17.943628),
            point(2470, 17.767982),
            point(2475, 17.485325),
            point(2480, 17.274485),
            point(2485, 16.860285),
            point(2490, 16.39771),
            point(2495, 15.8786335),
            point(2500, 15.197955),
            point(2505, 14.395715),
            point(2510, 13.626046),
            point(2515, 12.731712),
            point(2520, 12.030859),
            point(2525, 11.369283),
            point(2530, 10.884773),
            point(2535, 10.300518),
            point(2540, 9.759236),
            point(2545, 9.200565),
            point(2550, 8.160665),
            point(2555, 6.027586),
            point(2560, 7.025402),
            point(2565, 7.3432693),
            point(2570, 7.21131),
            point(2575, 7.5342693),
            point(2580, 7.7479978),
            point(2585, 7.391924),
            point(2590, 7.1097393),
            point(2595, 6.7781672),
            point(2600, 6.909233),
            point(2605, 7.241814),
            point(2610, 7.6876593),
            point(2615, 8.080476),
            point(2620, 8.832734),
            point(2625, 9.562958),
            point(2630, 10.351854),
            point(2635, 11.533312),
            point(2640, 12.621552),
            point(2645, 12.91216),
            point(2650, 13.380138),
            point(2655, 12.902913),
            point(2660, 11.457884),
            point(2665, 9.262649),
            point(2670, 8.012302),
            point(2675, 7.4591155),
            point(2680, 7.077675)
        )

        val result = detector.analyze(jumpSession)

        assertNotNull(result)

        assertEquals(1850L, result!!.takeOffTime)
        assertEquals(2235L, result.landingTime)
        assertEquals(385L, result.flightTime)
    }
}