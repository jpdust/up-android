package com.unstampedpages.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencySymbolsTest {

    // ---------------------------------------------------------------------------
    // Exhaustive mapping tests — every code present in CountryRepository
    // ---------------------------------------------------------------------------

    @Test fun getSymbol_aed() = assertEquals("د.إ", CurrencySymbols.getSymbol("AED"))
    @Test fun getSymbol_afn() = assertEquals("؋",   CurrencySymbols.getSymbol("AFN"))
    @Test fun getSymbol_all() = assertEquals("L",   CurrencySymbols.getSymbol("ALL"))
    @Test fun getSymbol_amd() = assertEquals("֏",   CurrencySymbols.getSymbol("AMD"))
    @Test fun getSymbol_aoa() = assertEquals("Kz",  CurrencySymbols.getSymbol("AOA"))
    @Test fun getSymbol_ars() = assertEquals("$",   CurrencySymbols.getSymbol("ARS"))
    @Test fun getSymbol_aud() = assertEquals("$",   CurrencySymbols.getSymbol("AUD"))
    @Test fun getSymbol_awg() = assertEquals("ƒ",   CurrencySymbols.getSymbol("AWG"))
    @Test fun getSymbol_azn() = assertEquals("₼",   CurrencySymbols.getSymbol("AZN"))
    @Test fun getSymbol_bam() = assertEquals("KM",  CurrencySymbols.getSymbol("BAM"))
    @Test fun getSymbol_bbd() = assertEquals("$",   CurrencySymbols.getSymbol("BBD"))
    @Test fun getSymbol_bdt() = assertEquals("৳",   CurrencySymbols.getSymbol("BDT"))
    @Test fun getSymbol_bgn() = assertEquals("лв",  CurrencySymbols.getSymbol("BGN"))
    @Test fun getSymbol_bhd() = assertEquals("BD",  CurrencySymbols.getSymbol("BHD"))
    @Test fun getSymbol_bif() = assertEquals("Fr",  CurrencySymbols.getSymbol("BIF"))
    @Test fun getSymbol_bmd() = assertEquals("$",   CurrencySymbols.getSymbol("BMD"))
    @Test fun getSymbol_bnd() = assertEquals("$",   CurrencySymbols.getSymbol("BND"))
    @Test fun getSymbol_bob() = assertEquals("Bs.", CurrencySymbols.getSymbol("BOB"))
    @Test fun getSymbol_brl() = assertEquals("R$",  CurrencySymbols.getSymbol("BRL"))
    @Test fun getSymbol_bsd() = assertEquals("$",   CurrencySymbols.getSymbol("BSD"))
    @Test fun getSymbol_btn() = assertEquals("Nu",  CurrencySymbols.getSymbol("BTN"))
    @Test fun getSymbol_bwp() = assertEquals("P",   CurrencySymbols.getSymbol("BWP"))
    @Test fun getSymbol_byn() = assertEquals("Br",  CurrencySymbols.getSymbol("BYN"))
    @Test fun getSymbol_bzd() = assertEquals("$",   CurrencySymbols.getSymbol("BZD"))
    @Test fun getSymbol_cad() = assertEquals("$",   CurrencySymbols.getSymbol("CAD"))
    @Test fun getSymbol_cdf() = assertEquals("Fr",  CurrencySymbols.getSymbol("CDF"))
    @Test fun getSymbol_chf() = assertEquals("Fr",  CurrencySymbols.getSymbol("CHF"))
    @Test fun getSymbol_clp() = assertEquals("$",   CurrencySymbols.getSymbol("CLP"))
    @Test fun getSymbol_cny() = assertEquals("¥",   CurrencySymbols.getSymbol("CNY"))
    @Test fun getSymbol_cop() = assertEquals("$",   CurrencySymbols.getSymbol("COP"))
    @Test fun getSymbol_crc() = assertEquals("₡",   CurrencySymbols.getSymbol("CRC"))
    @Test fun getSymbol_cup() = assertEquals("$",   CurrencySymbols.getSymbol("CUP"))
    @Test fun getSymbol_cve() = assertEquals("$",   CurrencySymbols.getSymbol("CVE"))
    @Test fun getSymbol_czk() = assertEquals("Kč",  CurrencySymbols.getSymbol("CZK"))
    @Test fun getSymbol_djf() = assertEquals("Fr",  CurrencySymbols.getSymbol("DJF"))
    @Test fun getSymbol_dkk() = assertEquals("kr",  CurrencySymbols.getSymbol("DKK"))
    @Test fun getSymbol_dop() = assertEquals("$",   CurrencySymbols.getSymbol("DOP"))
    @Test fun getSymbol_dzd() = assertEquals("دج",  CurrencySymbols.getSymbol("DZD"))
    @Test fun getSymbol_egp() = assertEquals("£",   CurrencySymbols.getSymbol("EGP"))
    @Test fun getSymbol_ern() = assertEquals("Nfk", CurrencySymbols.getSymbol("ERN"))
    @Test fun getSymbol_etb() = assertEquals("Br",  CurrencySymbols.getSymbol("ETB"))
    @Test fun getSymbol_eur() = assertEquals("€",   CurrencySymbols.getSymbol("EUR"))
    @Test fun getSymbol_fjd() = assertEquals("$",   CurrencySymbols.getSymbol("FJD"))
    @Test fun getSymbol_fkp() = assertEquals("£",   CurrencySymbols.getSymbol("FKP"))
    @Test fun getSymbol_gbp() = assertEquals("£",   CurrencySymbols.getSymbol("GBP"))
    @Test fun getSymbol_gel() = assertEquals("₾",   CurrencySymbols.getSymbol("GEL"))
    @Test fun getSymbol_ghs() = assertEquals("₵",   CurrencySymbols.getSymbol("GHS"))
    @Test fun getSymbol_gip() = assertEquals("£",   CurrencySymbols.getSymbol("GIP"))
    @Test fun getSymbol_gmd() = assertEquals("D",   CurrencySymbols.getSymbol("GMD"))
    @Test fun getSymbol_gnf() = assertEquals("Fr",  CurrencySymbols.getSymbol("GNF"))
    @Test fun getSymbol_gtq() = assertEquals("Q",   CurrencySymbols.getSymbol("GTQ"))
    @Test fun getSymbol_gyd() = assertEquals("$",   CurrencySymbols.getSymbol("GYD"))
    @Test fun getSymbol_hkd() = assertEquals("$",   CurrencySymbols.getSymbol("HKD"))
    @Test fun getSymbol_hnl() = assertEquals("L",   CurrencySymbols.getSymbol("HNL"))
    @Test fun getSymbol_htg() = assertEquals("G",   CurrencySymbols.getSymbol("HTG"))
    @Test fun getSymbol_huf() = assertEquals("Ft",  CurrencySymbols.getSymbol("HUF"))
    @Test fun getSymbol_idr() = assertEquals("Rp",  CurrencySymbols.getSymbol("IDR"))
    @Test fun getSymbol_ils() = assertEquals("₪",   CurrencySymbols.getSymbol("ILS"))
    @Test fun getSymbol_inr() = assertEquals("₹",   CurrencySymbols.getSymbol("INR"))
    @Test fun getSymbol_iqd() = assertEquals("ع.د", CurrencySymbols.getSymbol("IQD"))
    @Test fun getSymbol_irr() = assertEquals("﷼",   CurrencySymbols.getSymbol("IRR"))
    @Test fun getSymbol_isk() = assertEquals("kr",  CurrencySymbols.getSymbol("ISK"))
    @Test fun getSymbol_jmd() = assertEquals("$",   CurrencySymbols.getSymbol("JMD"))
    @Test fun getSymbol_jod() = assertEquals("JD",  CurrencySymbols.getSymbol("JOD"))
    @Test fun getSymbol_jpy() = assertEquals("¥",   CurrencySymbols.getSymbol("JPY"))
    @Test fun getSymbol_kes() = assertEquals("KSh", CurrencySymbols.getSymbol("KES"))
    @Test fun getSymbol_kgs() = assertEquals("с",   CurrencySymbols.getSymbol("KGS"))
    @Test fun getSymbol_khr() = assertEquals("៛",   CurrencySymbols.getSymbol("KHR"))
    @Test fun getSymbol_kmf() = assertEquals("Fr",  CurrencySymbols.getSymbol("KMF"))
    @Test fun getSymbol_kpw() = assertEquals("₩",   CurrencySymbols.getSymbol("KPW"))
    @Test fun getSymbol_krw() = assertEquals("₩",   CurrencySymbols.getSymbol("KRW"))
    @Test fun getSymbol_kwd() = assertEquals("KD",  CurrencySymbols.getSymbol("KWD"))
    @Test fun getSymbol_kyd() = assertEquals("$",   CurrencySymbols.getSymbol("KYD"))
    @Test fun getSymbol_kzt() = assertEquals("₸",   CurrencySymbols.getSymbol("KZT"))
    @Test fun getSymbol_lak() = assertEquals("₭",   CurrencySymbols.getSymbol("LAK"))
    @Test fun getSymbol_lbp() = assertEquals("£",   CurrencySymbols.getSymbol("LBP"))
    @Test fun getSymbol_lkr() = assertEquals("₨",   CurrencySymbols.getSymbol("LKR"))
    @Test fun getSymbol_lrd() = assertEquals("$",   CurrencySymbols.getSymbol("LRD"))
    @Test fun getSymbol_lsl() = assertEquals("L",   CurrencySymbols.getSymbol("LSL"))
    @Test fun getSymbol_lyd() = assertEquals("LD",  CurrencySymbols.getSymbol("LYD"))
    @Test fun getSymbol_mad() = assertEquals("د.م.",CurrencySymbols.getSymbol("MAD"))
    @Test fun getSymbol_mdl() = assertEquals("L",   CurrencySymbols.getSymbol("MDL"))
    @Test fun getSymbol_mga() = assertEquals("Ar",  CurrencySymbols.getSymbol("MGA"))
    @Test fun getSymbol_mkd() = assertEquals("ден", CurrencySymbols.getSymbol("MKD"))
    @Test fun getSymbol_mmk() = assertEquals("K",   CurrencySymbols.getSymbol("MMK"))
    @Test fun getSymbol_mnt() = assertEquals("₮",   CurrencySymbols.getSymbol("MNT"))
    @Test fun getSymbol_mru() = assertEquals("UM",  CurrencySymbols.getSymbol("MRU"))
    @Test fun getSymbol_mur() = assertEquals("₨",   CurrencySymbols.getSymbol("MUR"))
    @Test fun getSymbol_mvr() = assertEquals("Rf",  CurrencySymbols.getSymbol("MVR"))
    @Test fun getSymbol_mwk() = assertEquals("MK",  CurrencySymbols.getSymbol("MWK"))
    @Test fun getSymbol_mxn() = assertEquals("$",   CurrencySymbols.getSymbol("MXN"))
    @Test fun getSymbol_myr() = assertEquals("RM",  CurrencySymbols.getSymbol("MYR"))
    @Test fun getSymbol_mzn() = assertEquals("MT",  CurrencySymbols.getSymbol("MZN"))
    @Test fun getSymbol_nad() = assertEquals("$",   CurrencySymbols.getSymbol("NAD"))
    @Test fun getSymbol_ngn() = assertEquals("₦",   CurrencySymbols.getSymbol("NGN"))
    @Test fun getSymbol_nio() = assertEquals("C$",  CurrencySymbols.getSymbol("NIO"))
    @Test fun getSymbol_nok() = assertEquals("kr",  CurrencySymbols.getSymbol("NOK"))
    @Test fun getSymbol_npr() = assertEquals("₨",   CurrencySymbols.getSymbol("NPR"))
    @Test fun getSymbol_nzd() = assertEquals("$",   CurrencySymbols.getSymbol("NZD"))
    @Test fun getSymbol_omr() = assertEquals("﷼",   CurrencySymbols.getSymbol("OMR"))
    @Test fun getSymbol_pen() = assertEquals("S/",  CurrencySymbols.getSymbol("PEN"))
    @Test fun getSymbol_pgk() = assertEquals("K",   CurrencySymbols.getSymbol("PGK"))
    @Test fun getSymbol_php() = assertEquals("₱",   CurrencySymbols.getSymbol("PHP"))
    @Test fun getSymbol_pkr() = assertEquals("₨",   CurrencySymbols.getSymbol("PKR"))
    @Test fun getSymbol_pln() = assertEquals("zł",  CurrencySymbols.getSymbol("PLN"))
    @Test fun getSymbol_pyg() = assertEquals("₲",   CurrencySymbols.getSymbol("PYG"))
    @Test fun getSymbol_qar() = assertEquals("﷼",   CurrencySymbols.getSymbol("QAR"))
    @Test fun getSymbol_ron() = assertEquals("lei", CurrencySymbols.getSymbol("RON"))
    @Test fun getSymbol_rsd() = assertEquals("din", CurrencySymbols.getSymbol("RSD"))
    @Test fun getSymbol_rub() = assertEquals("₽",   CurrencySymbols.getSymbol("RUB"))
    @Test fun getSymbol_rwf() = assertEquals("Fr",  CurrencySymbols.getSymbol("RWF"))
    @Test fun getSymbol_sar() = assertEquals("﷼",   CurrencySymbols.getSymbol("SAR"))
    @Test fun getSymbol_sbd() = assertEquals("$",   CurrencySymbols.getSymbol("SBD"))
    @Test fun getSymbol_scr() = assertEquals("₨",   CurrencySymbols.getSymbol("SCR"))
    @Test fun getSymbol_sdg() = assertEquals("£",   CurrencySymbols.getSymbol("SDG"))
    @Test fun getSymbol_sek() = assertEquals("kr",  CurrencySymbols.getSymbol("SEK"))
    @Test fun getSymbol_sgd() = assertEquals("$",   CurrencySymbols.getSymbol("SGD"))
    @Test fun getSymbol_shp() = assertEquals("£",   CurrencySymbols.getSymbol("SHP"))
    @Test fun getSymbol_sle() = assertEquals("Le",  CurrencySymbols.getSymbol("SLE"))
    @Test fun getSymbol_sls() = assertEquals("Sl",  CurrencySymbols.getSymbol("SLS"))
    @Test fun getSymbol_sos() = assertEquals("Sh",  CurrencySymbols.getSymbol("SOS"))
    @Test fun getSymbol_srd() = assertEquals("$",   CurrencySymbols.getSymbol("SRD"))
    @Test fun getSymbol_ssp() = assertEquals("£",   CurrencySymbols.getSymbol("SSP"))
    @Test fun getSymbol_stn() = assertEquals("Db",  CurrencySymbols.getSymbol("STN"))
    @Test fun getSymbol_syp() = assertEquals("£",   CurrencySymbols.getSymbol("SYP"))
    @Test fun getSymbol_szl() = assertEquals("L",   CurrencySymbols.getSymbol("SZL"))
    @Test fun getSymbol_thb() = assertEquals("฿",   CurrencySymbols.getSymbol("THB"))
    @Test fun getSymbol_tjs() = assertEquals("SM",  CurrencySymbols.getSymbol("TJS"))
    @Test fun getSymbol_tmt() = assertEquals("T",   CurrencySymbols.getSymbol("TMT"))
    @Test fun getSymbol_tnd() = assertEquals("DT",  CurrencySymbols.getSymbol("TND"))
    @Test fun getSymbol_top() = assertEquals("T$",  CurrencySymbols.getSymbol("TOP"))
    @Test fun getSymbol_try_() = assertEquals("₺",  CurrencySymbols.getSymbol("TRY"))
    @Test fun getSymbol_ttd() = assertEquals("$",   CurrencySymbols.getSymbol("TTD"))
    @Test fun getSymbol_twd() = assertEquals("$",   CurrencySymbols.getSymbol("TWD"))
    @Test fun getSymbol_tzs() = assertEquals("Sh",  CurrencySymbols.getSymbol("TZS"))
    @Test fun getSymbol_uah() = assertEquals("₴",   CurrencySymbols.getSymbol("UAH"))
    @Test fun getSymbol_ugx() = assertEquals("Sh",  CurrencySymbols.getSymbol("UGX"))
    @Test fun getSymbol_usd() = assertEquals("$",   CurrencySymbols.getSymbol("USD"))
    @Test fun getSymbol_uyu() = assertEquals("$",   CurrencySymbols.getSymbol("UYU"))
    @Test fun getSymbol_uzs() = assertEquals("лв",  CurrencySymbols.getSymbol("UZS"))
    @Test fun getSymbol_ves() = assertEquals("Bs.S",CurrencySymbols.getSymbol("VES"))
    @Test fun getSymbol_vnd() = assertEquals("₫",   CurrencySymbols.getSymbol("VND"))
    @Test fun getSymbol_vuv() = assertEquals("Vt",  CurrencySymbols.getSymbol("VUV"))
    @Test fun getSymbol_wst() = assertEquals("T",   CurrencySymbols.getSymbol("WST"))
    @Test fun getSymbol_xaf() = assertEquals("Fr",  CurrencySymbols.getSymbol("XAF"))
    @Test fun getSymbol_xcd() = assertEquals("$",   CurrencySymbols.getSymbol("XCD"))
    @Test fun getSymbol_xof() = assertEquals("Fr",  CurrencySymbols.getSymbol("XOF"))
    @Test fun getSymbol_xpf() = assertEquals("Fr",  CurrencySymbols.getSymbol("XPF"))
    @Test fun getSymbol_yer() = assertEquals("﷼",   CurrencySymbols.getSymbol("YER"))
    @Test fun getSymbol_zar() = assertEquals("R",   CurrencySymbols.getSymbol("ZAR"))
    @Test fun getSymbol_zmw() = assertEquals("ZK",  CurrencySymbols.getSymbol("ZMW"))

    // ---------------------------------------------------------------------------
    // Fallback behaviour
    // ---------------------------------------------------------------------------

    @Test
    fun getSymbol_unknownCode_returnsDollarFallback() {
        assertEquals("$", CurrencySymbols.getSymbol("XYZ"))
    }

    @Test
    fun getSymbol_emptyCode_returnsDollarFallback() {
        assertEquals("$", CurrencySymbols.getSymbol(""))
    }

    @Test
    fun getSymbol_lowercaseCode_returnsDollarFallback() {
        // Map keys are uppercase ISO 4217; lowercase bypasses the lookup
        assertEquals("$", CurrencySymbols.getSymbol("gbp"))
    }

    // ---------------------------------------------------------------------------
    // Sanity: every returned symbol is a non-empty string
    // ---------------------------------------------------------------------------

    @Test
    fun getSymbol_allKnownCodes_returnNonEmptySymbol() {
        val knownCodes = listOf(
            "AED","AFN","ALL","AMD","AOA","ARS","AUD","AWG","AZN","BAM","BBD","BDT",
            "BGN","BHD","BIF","BMD","BND","BOB","BRL","BSD","BTN","BWP","BYN","BZD",
            "CAD","CDF","CHF","CLP","CNY","COP","CRC","CUP","CVE","CZK","DJF","DKK",
            "DOP","DZD","EGP","ERN","ETB","EUR","FJD","FKP","GBP","GEL","GHS","GIP",
            "GMD","GNF","GTQ","GYD","HKD","HNL","HTG","HUF","IDR","ILS","INR","IQD",
            "IRR","ISK","JMD","JOD","JPY","KES","KGS","KHR","KMF","KPW","KRW","KWD",
            "KYD","KZT","LAK","LBP","LKR","LRD","LSL","LYD","MAD","MDL","MGA","MKD",
            "MMK","MNT","MRU","MUR","MVR","MWK","MXN","MYR","MZN","NAD","NGN","NIO",
            "NOK","NPR","NZD","OMR","PEN","PGK","PHP","PKR","PLN","PYG","QAR","RON",
            "RSD","RUB","RWF","SAR","SBD","SCR","SDG","SEK","SGD","SHP","SLE","SLS",
            "SOS","SRD","SSP","STN","SYP","SZL","THB","TJS","TMT","TND","TOP","TRY",
            "TTD","TWD","TZS","UAH","UGX","USD","UYU","UZS","VES","VND","VUV","WST",
            "XAF","XCD","XOF","XPF","YER","ZAR","ZMW"
        )
        knownCodes.forEach { code ->
            val symbol = CurrencySymbols.getSymbol(code)
            assertTrue("Symbol for $code must be non-empty", symbol.isNotEmpty())
        }
    }
}
