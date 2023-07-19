package org.komputing.kbms

import org.komputing.kbms.model.BatteryInfo
import java.nio.ByteBuffer

class BMSPacketParser {
    private var currentPackage = ByteArray(0)

    var batteryInfo = BatteryInfo()

    var invalidPackages = 0
    var validPackages = 0

    private class FramedPackage(
        val type: Byte,
        val payload: ByteArray,
        val checksum: Short
    )

    fun parsePacket(value: ByteArray, onChange: () -> Unit = {}) {
        if (value.first() == 0xd.toByte()) {
            currentPackage = value
        } else {
            currentPackage += value
        }

        if (currentPackage.last() == 0x77.toByte()) {
            if (currentPackage.size < 5) { //below minimal package size
                invalidPackages++
                return
            }
            val size = ByteBuffer.wrap(currentPackage.copyOfRange(2, 4)).short

            if (size.toInt()!=currentPackage.size - 7) {
                invalidPackages++
                return
            }
            val framedPackage = FramedPackage(
                type = currentPackage[1],

                payload = currentPackage.copyOfRange(4, currentPackage.size - 3),
                checksum = ByteBuffer.wrap(
                    currentPackage.copyOfRange(
                        currentPackage.size - 3,
                        currentPackage.size - 1
                    )
                ).short
            )

            val payload = currentPackage.copyOfRange(2, currentPackage.size - 3)
            var crc = 0x10000
            payload.forEach { crc -= it.toUByte().toInt() }

            if (framedPackage.checksum.toUShort() != crc.toUShort()) {
                invalidPackages++
                return
            }

            validPackages++

            processPackage(framedPackage, onChange)
            currentPackage = ByteArray(0)
        }
    }

    private fun processPackage(framedPackage: FramedPackage, onChange: () -> Unit) {
        when (framedPackage.type.toInt()) {
            3 -> {
                val buffer = ByteBuffer.wrap(framedPackage.payload)

                batteryInfo = batteryInfo.copy(
                    voltage = buffer.short.toUShort(),
                    current = buffer.short,
                    residualCapacity = buffer.short.toUShort(),
                    nominalCapacity = buffer.short.toUShort(),
                    cycleLife = buffer.short.toUShort(),
                    productionDate = buffer.short.toUShort(),
                    balanceStatus = buffer.short.toUShort(),
                    balanceStatus2 = buffer.short.toUShort(),
                    protectionStatus = buffer.short.toUShort(),
                    version = buffer.get().toUByte(),
                    rsoc = buffer.get().toUByte(),
                    controlStatus = buffer.get().toUByte(),
                    cellsInSeriesCount = buffer.get().toUByte(),
                    tempSensorCount = buffer.get().toUByte(),
                )
                batteryInfo = batteryInfo.copy(
                    temperatures = (0..<batteryInfo.tempSensorCount!!.toInt()).map {
                        (buffer.short - 2731).toUShort()
                    }
                )
                onChange.invoke()
            }

            4 -> {
                val cellList: MutableList<Short> = mutableListOf()
                val buff = ByteBuffer.wrap(framedPackage.payload)

                while (buff.hasRemaining()) {
                    cellList.add(buff.short)
                }
                batteryInfo = batteryInfo.copy(cellVoltages = cellList)
                onChange.invoke()
            }
        }
    }
}