package org.komputing.kbms

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class TheParser {

    private fun BMSPacketParser.process(vararg elements: Int) = parsePacket(elements.map { it.toByte() }.toByteArray())

    @Test
    fun canReadVoltage() {
        val parser = BMSPacketParser()

        parser.process(-35, 3, 0, 27, 17, -76, 0, -12, 1, -50, 12, 12, 0, 20, 44, 84, 10, -86, 0, 0, 0, 0, 34, 15, 3, 13, 2, 11, -66, 11, -91, -6, 65, 119)

        assertThat(parser.batteryInfo.voltage).isEqualTo(4532.toUShort())

        assertThat(parser.validPackages).isEqualTo(1)
    }

    @Test
    fun canReadVoltageFomSplitPacket() {
        val parser = BMSPacketParser()

        parser.process(-35, 3, 0, 27, 17, -76, 0, -12, 1, -50, 12, 12, 0, 20, 44, 84, 10, -86)
        parser.process(0, 0, 0, 0, 34, 15, 3, 13, 2, 11, -66, 11, -91, -6, 65, 119)

        assertThat(parser.batteryInfo.voltage).isEqualTo(4532.toUShort())
        assertThat(parser.validPackages).isEqualTo(1)
    }

    @Test
    fun canHandleGoodChecksum() {
        val parser = BMSPacketParser()

        parser.process(-35, 3, 0, 27, 17, -76, 0, -12, 1, -50, 12, 12, 0, 20, 44, 84, 10, -86, 0, 0, 0, 0, 34, 15, 3, 13, 2, 11, -66, 11, -91, -6, 65, 119)

        assertThat(parser.validPackages).isEqualTo(1)
        assertThat(parser.invalidPackages).isEqualTo(0)
    }

    @Test
    fun canDetectBadChecksum() {
        val parser = BMSPacketParser()

        parser.process(-35, 3, 0, 27, 17, -76, 0, -12, 1, -50, 12, 12, 0, 20, 44, 84, 10, -86, 0, 0, 0, 0, 34, 15, 3, 13, 2, 11, -66, 11, -91, -6, 66, 119)

        assertThat(parser.invalidPackages).isEqualTo(1)
        assertThat(parser.validPackages).isEqualTo(0)
    }

    @Test
    fun canHandleBadPacket() {
        val parser = BMSPacketParser()

        parser.process(-35, 119)

        assertThat(parser.invalidPackages).isEqualTo(1)
        assertThat(parser.validPackages).isEqualTo(0)
    }

}