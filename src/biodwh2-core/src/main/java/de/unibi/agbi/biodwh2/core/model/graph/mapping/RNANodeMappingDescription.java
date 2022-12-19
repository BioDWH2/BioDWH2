package de.unibi.agbi.biodwh2.core.model.graph.mapping;

import de.unibi.agbi.biodwh2.core.model.graph.NodeMappingDescription;

import java.util.HashMap;
import java.util.Map;

public class RNANodeMappingDescription extends NodeMappingDescription {
    public enum RNAType {
        UNKNOWN(null),
        /**
         * messenger RNA
         */
        M_RNA("mRNA"),
        /**
         * transfer RNA
         */
        T_RNA("tRNA"),
        /**
         * ribosomal RNA
         */
        R_RNA("rRNA"),
        /**
         * micro RNA
         */
        MI_RNA("miRNA"),
        /**
         * small conditional RNA
         */
        SC_RNA("scRNA"),
        /**
         * small nuclear RNA
         */
        SN_RNA("snRNA"),
        /**
         * non-coding RNA
         */
        // TODO: decide categorical entries such as NC_RNA("ncRNA"),
        /**
         * long non-coding RNA
         */
        LNC_RNA("lncRNA"),
        /**
         * small non-coding RNA
         */
        SNC_RNA("sncRNA"),
        /**
         * small necleolar RNA
         */
        SNO_RNA("snoRNA"),
        /**
         * vault RNA
         */
        VT_RNA("vtRNA"),
        Y_RNA("Y_RNA"),
        /**
         * enhancer RNA
         */
        E_RNA("eRNA"),
        /**
         * PIWI-interacting RNA
         */
        PI_RNA("piRNA"),
        RIBOZYME("ribozyme"),
        ANTISENSE_RNA("antisense_RNA");
        /*
        circRNA -   circular RNA
        lincRNA -   long intergenic non-coding RNA
        scaRNA  -   small cajal body-specific RNA
        shRNA   -   short hairpin RNA
        sRNA    -   bacterial small RNA
        Mt_tRNA
        misc_RNA
        non_stop_decay
        nonsense_mediated_decay
        PCG
        pseudo
        repeats
        retained_intron
        TEC
        TR_C_gene
        tRF
        unassigned RNA
        guide_RNA
        RNase_MRP_RNA
        RNase_P_RNA
        SRP_RNA
        telomerase_RNA
        processed_transcript
        primary_transcript
        transcript
        */

        public final String value;

        RNAType(final String value) {
            this.value = value;
        }
    }

    private final RNAType rnaType;

    public RNANodeMappingDescription(final RNAType rnaType) {
        super(NodeType.RNA);
        this.rnaType = rnaType;
    }

    public RNAType getRnaType() {
        return rnaType;
    }

    @Override
    public Map<String, Object> getAdditionalProperties() {
        final Map<String, Object> result = new HashMap<>();
        result.put("rna_type", rnaType.value);
        return result;
    }
}
