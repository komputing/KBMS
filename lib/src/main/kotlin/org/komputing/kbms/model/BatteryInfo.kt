package org.komputing.kbms.model

data class BatteryInfo(
    var cellVoltages: MutableList<Short> = mutableListOf(),
    var voltage: UShort? = null,
    var current: Short? = null,
    var residualCapacity: UShort? = null,
    var nominalCapacity: UShort? = null,
    var cycleLife: UShort? = null,
    var productionDate: UShort? = null,
    var balanceStatus: UShort? = null,
    var balanceStatus2: UShort? = null,
    var protectionStatus: UShort? = null,
    var version: UByte? = null,
    var rsoc: UByte? = null,
    var controlStatus: UByte? = null,
    var cellsInSeriesCount: UByte? = null,
    var tempSensorCount: UByte? = null,
    val temperatures: List<UShort> = emptyList()
)