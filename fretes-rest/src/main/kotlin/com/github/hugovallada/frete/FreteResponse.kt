package com.github.hugovallada.frete

import com.github.hugovallada.CalculaFreteResponse
import java.math.BigDecimal

data class FreteResponse(val cep: String, val valor: BigDecimal){
    constructor(calculaFreteResponse: CalculaFreteResponse) : this(calculaFreteResponse.cep, calculaFreteResponse.valor.toBigDecimal())
}
