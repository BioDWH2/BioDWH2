package de.unibi.agbi.biodwh2.abdamed2.io.k2;

@SuppressWarnings("unused")
public enum DataType {
    /**
     * [A-Z]*
     */
    AL1,
    /**
     *
     */
    AN1,
    /**
     * [A-Z0-9]*
     */
    AN2,
    /**
     *
     */
    AN3,
    /**
     * ATC code
     * <p>[A-Z]([0-9]{2}([A-Z]([A-Z]([0-9]{2})?)?)?)?</p>
     * <p>X</p>
     * <p>Xnn</p>
     * <p>XnnX</p>
     * <p>XnnXX</p>
     * <p>XnnXXnn</p>
     */
    ATC,
    /**
     * Base64 encoded 8 bit binary data
     */
    B64,
    /**
     * Date
     * <p>[0-9]{8}</p>
     * <p>YYYYMMDD</p>
     */
    DT8,
    /**
     * Month and year
     * <p>[0-9]{6}</p>
     * <p>MMYYYY</p>
     */
    DTV,
    /**
     * Flag (0|1)
     */
    FLA,
    /**
     * [0-9A-Z_]*
     */
    FN1,
    /**
     * [-_0-9A-Za-z]*
     */
    FN2,
    /**
     * [0-9]*(\.[0-9]*)?
     */
    GK1,
    /**
     *
     */
    GRU,
    /**
     * [0-9A-Za-z_]*
     */
    ID1,
    /**
     *
     */
    IKZ,
    /**
     *
     */
    IND,
    /**
     *
     */
    MPG,
    /**
     *
     */
    NU1,
    /**
     *
     */
    NU2,
    /**
     *
     */
    NU3,
    /**
     *
     */
    NU4,
    /**
     *
     */
    PNH,
    /**
     *
     */
    PRO,
    /**
     *
     */
    PZN,
    /**
     *
     */
    PZ8,
    /**
     *
     */
    WGS
}
