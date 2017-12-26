package me.cybulski.civ5pbemserver.civilization;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Michał Cybulski
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum Civilization {
    AMERICAN("American", "Washington"),
    ARABIAN("Arabian", "Harun al-Rashid"),
    ASSYRIAN("Assyrian", "Ashurbanipal"),
    AUSTRIAN("Austrian", "Maria Theresa"),
    AZTEC("Aztec", "Montezuma"),
    BABYLONIAN("Babylonian", "Nebuchadnezzar II"),
    BRAZILIAN("Brazilian", "Pedro II"),
    BYZANTINE("Byzantine", "Theodora"),
    CARTHAGINIAN("Carthaginian", "Dido"),
    CELTIC("Celtic", "Boudicca"),
    CHINESE("Chinese", "Wu Zetian"),
    DANISH("Danish", "Harald Bluetooth"),
    DUTCH("Dutch", "William"),
    EGYPTIAN("Egyptian", "Ramesses II"),
    ENGLISH("English", "Elizabeth"),
    ETHIOPIAN("Ethiopian", "Haile Selassie"),
    FRENCH("French", "Napoleon"),
    GERMAN("German", "Bismarck"),
    GREEK("Greek", "Alexander"),
    HUNNIC("Hunnic", "Attila"),
    INCAN("Incan", "Pachacuti"),
    INDIAN("Indian", "Gandhi"),
    INDONESIAN("Indonesian", "Gajah Mada"),
    IROQUOIS("Iroquois", "Hiawatha"),
    JAPANESE("Japanese", "Oda Nobunaga"),
    KOREAN("Korean", "Sejong"),
    MAYAN("Mayan", "Pacal"),
    MONGOLIAN("Mongolian", "Genghis Khan"),
    MOROCCAN("Moroccan", "Ahmad al-Mansur"),
    OTTOMAN("Ottoman", "Suleiman"),
    PERSIAN("Persian", "Darius I"),
    POLISH("Polish", "Casimir III"),
    POLYNESIAN("Polynesian", "Kamehameha"),
    PORTUGUESE("Portuguese", "Maria I"),
    ROMAN("Roman", "Augustus Caesar"),
    RUSSIAN("Russian", "Catherine"),
    SHOSHONE("Shoshone", "Pocatello"),
    SIAMESE("Siamese", "Ramkhamhaeng"),
    SONGHAI("Songhai", "Askia"),
    SPANISH("Spanish", "Isabella"),
    SWEDISH("Swedish", "Gustavus Adolphus"),
    VENETIAN("Venetian", "Enrico Dandolo"),
    ZULU("Zulu", "Shaka"),
    RANDOM("?", "?");

    private final String name;
    private final String leader;
}
