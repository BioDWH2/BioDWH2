package de.unibi.agbi.biodwh2.core.mapping;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public final class SpeciesLookup {
    public static final Entry ARABIDOPSIS_THALIANA = new Entry(null, "Arabidopsis thaliana", 3702, null);
    public static final Entry BOMBYX_MORI = new Entry(null, "Bombyx mori", 7091, null);
    public static final Entry BOS_TAURUS = new Entry(null, "Bos taurus", 9913, null);
    public static final Entry CAENORHABDITIS_ELEGANS = new Entry(null, "Caenorhabditis elegans", 6239, null);
    public static final Entry CANIS_FAMILIARIS = new Entry(null, "Canis familiaris", 9615, null);
    public static final Entry CAPRA_HIRCUS = new Entry(null, "Capra hircus", 9925, null);
    public static final Entry CRICETULUS_GRISEUS = new Entry(null, "Cricetulus griseus", 10029, null);
    public static final Entry DANIO_RERIO = new Entry("dre", "Danio rerio", 7955, "DAR");
    public static final Entry DROSOPHILA_MELANOGASTER = new Entry(null, "Drosophila melanogaster", 7227, null);
    public static final Entry EPSTEIN_BARR_VIRUS = new Entry(null, "Epstein Barr virus", 10376, null);
    public static final Entry EQUUS_CABALLUS = new Entry(null, "Equus caballus", 9796, null);
    public static final Entry GALLID_ALPHAHERPESVIRUS_2 = new Entry(null, "Gallid alphaherpesvirus 2", 10390, null);
    public static final Entry GALLUS_GALLUS = new Entry("gga", "Gallus gallus", 9031, "GAL");
    public static final Entry GLYCINE_MAX = new Entry(null, "Glycine max", 3847, null);
    public static final Entry GORILLA_GORILLA = new Entry(null, "Gorilla gorilla", 9593, null);
    public static final Entry HOMO_SAPIENS = new Entry("hsa", "Homo sapiens", 9606, "");
    public static final Entry HUMAN_CYTOMEGALOVIRUS = new Entry(null, "Human cytomegalovirus", 10359, null);
    // Acronym: KSHV
    public static final Entry KAPOSI_SARCOMA_ASSOCIATED_HERPESVIRUS = new Entry(null,
                                                                                "Kaposi sarcoma-associated herpesvirus",
                                                                                37296, null);
    public static final Entry MACACA_MULATTA = new Entry("mcc", "Macaca mulatta", 9544, "MMU");
    public static final Entry MACACA_NEMESTRINA = new Entry(null, "Macaca nemestrina", 9545, null);
    public static final Entry MEDICAGO_TRUNCATULA = new Entry(null, "Medicago truncatula", 3880, null);
    public static final Entry MUS_MUSCULUS = new Entry("mmu", "Mus musculus", 10090, "MUS");
    public static final Entry ORYZA_SATIVA = new Entry(null, "Oryza sativa", 4530, null);
    public static final Entry ORYZIAS_LATIPES = new Entry(null, "Oryzias latipes", 8090, null);
    public static final Entry OVIS_ARIES = new Entry(null, "Ovis aries", 9940, null);
    public static final Entry PAN_PANISCUS = new Entry(null, "Pan paniscus", 9597, null);
    public static final Entry PAN_TROGLODYTES = new Entry(null, "Pan troglodytes", 9598, null);
    public static final Entry PHYSCOMITRELLA_PATENS = new Entry(null, "Physcomitrella patens", 3218, null);
    public static final Entry PONGO_PYGMAEUS = new Entry(null, "Pongo pygmaeus", 9600, null);
    public static final Entry RATTUS_NORVEGICUS = new Entry("rno", "Rattus norvegicus", 10116, "RNO");
    public static final Entry SOLANUM_LYCOPERSICUM = new Entry(null, "Solanum lycopersicum", 4081, null);
    public static final Entry SUS_SCROFA = new Entry(null, "Sus scrofa", 9823, null);
    public static final Entry TAENIOPYGIA_GUTTATA = new Entry(null, "Taeniopygia guttata", 59729, null);
    public static final Entry VESICULAR_STOMATITIS_INDIANA_VIRUS = new Entry(null, "Vesicular stomatitis Indiana virus",
                                                                             null, null);
    public static final Entry VITIS_VINIFERA = new Entry(null, "Vitis vinifera", 29760, null);
    public static final Entry XENOPUS_LAEVIS = new Entry(null, "Xenopus laevis", 8355, null);
    public static final Entry XENOPUS_TROPICALIS = new Entry(null, "Xenopus tropicalis", 8364, null);
    public static final Entry ZEA_MAYS = new Entry(null, "Zea mays", 4577, null);
    public static final Entry AMPHIMEDON_QUEENSLANDICA = new Entry(null, "Amphimedon queenslandica", 400682, null);
    public static final Entry NEMATOSTELLA_VECTENSIS = new Entry(null, "Nematostella vectensis", 45351, null);
    public static final Entry HYDRA_MAGNIPAPILLATA = new Entry(null, "Hydra magnipapillata", 6085, null);
    public static final Entry SACCOGLOSSUS_KOWALEVSKII = new Entry(null, "Saccoglossus kowalevskii", 10224, null);
    public static final Entry STRONGYLOCENTROTUS_PURPURATUS = new Entry(null, "Strongylocentrotus purpuratus", 7668,
                                                                        null);
    public static final Entry CIONA_INTESTINALIS = new Entry(null, "Ciona intestinalis", 7719, null);
    public static final Entry CIONA_SAVIGNYI = new Entry(null, "Ciona savignyi", 51511, null);
    public static final Entry OIKOPLEURA_DIOICA = new Entry(null, "Oikopleura dioica", 34765, null);
    public static final Entry BRANCHIOSTOMA_FLORIDAE = new Entry(null, "Branchiostoma floridae", 7739, null);
    public static final Entry MONODELPHIS_DOMESTICA = new Entry(null, "Monodelphis domestica", 13616, null);
    public static final Entry ATELES_GEOFFROYI = new Entry(null, "Ateles geoffroyi", 9509, null);
    public static final Entry LAGOTHRIX_LAGOTRICHA = new Entry(null, "Lagothrix lagotricha", 9519, null);
    public static final Entry SAGUINUS_LABIATUS = new Entry(null, "Saguinus labiatus", 78454, null);
    public static final Entry PYGATHRIX_BIETI = new Entry(null, "Pygathrix bieti", 61621, null);
    public static final Entry SYMPHALANGUS_SYNDACTYLUS = new Entry(null, "Symphalangus syndactylus", 9590, null);
    public static final Entry LEMUR_CATTA = new Entry(null, "Lemur catta", 9447, null);
    public static final Entry ORNITHORHYNCHUS_ANATINUS = new Entry(null, "Ornithorhynchus anatinus", 9258, null);
    public static final Entry FUGU_RUBRIPES = new Entry(null, "Fugu rubripes", 31033, null);
    public static final Entry TETRAODON_NIGROVIRIDIS = new Entry(null, "Tetraodon nigroviridis", 99883, null);
    public static final Entry ANOPHELES_GAMBIAE = new Entry(null, "Anopheles gambiae", 7165, null);
    public static final Entry APIS_MELLIFERA = new Entry(null, "Apis mellifera", 7460, null);
    public static final Entry DROSOPHILA_ANANASSAE = new Entry(null, "Drosophila ananassae", 7217, null);
    public static final Entry DROSOPHILA_ERECTA = new Entry(null, "Drosophila erecta", 7220, null);
    public static final Entry DROSOPHILA_GRIMSHAWI = new Entry(null, "Drosophila grimshawi", 7222, null);
    public static final Entry DROSOPHILA_MOJAVENSIS = new Entry(null, "Drosophila mojavensis", 7230, null);
    public static final Entry DROSOPHILA_PERSIMILIS = new Entry(null, "Drosophila persimilis", 7234, null);
    public static final Entry DROSOPHILA_PSEUDOOBSCURA = new Entry(null, "Drosophila pseudoobscura", 7237, null);
    public static final Entry DROSOPHILA_SECHELLIA = new Entry(null, "Drosophila sechellia", 7238, null);
    public static final Entry DROSOPHILA_SIMULANS = new Entry(null, "Drosophila simulans", 7240, null);
    public static final Entry DROSOPHILA_VIRILIS = new Entry(null, "Drosophila virilis", 7244, null);
    public static final Entry DROSOPHILA_WILLISTONI = new Entry(null, "Drosophila willistoni", 7260, null);
    public static final Entry DROSOPHILA_YAKUBA = new Entry(null, "Drosophila yakuba", 7245, null);
    public static final Entry LOCUSTA_MIGRATORIA = new Entry(null, "Locusta migratoria", 7004, null);
    public static final Entry TRIBOLIUM_CASTANEUM = new Entry(null, "Tribolium castaneum", 7070, null);
    public static final Entry DAPHNIA_PULEX = new Entry(null, "Daphnia pulex", 6669, null);
    public static final Entry IXODES_SCAPULARIS = new Entry(null, "Ixodes scapularis", 6945, null);
    public static final Entry CAENORHABDITIS_BRIGGSAE = new Entry(null, "Caenorhabditis briggsae", 6238, null);
    public static final Entry SCHISTOSOMA_JAPONICUM = new Entry(null, "Schistosoma japonicum", 6182, null);
    public static final Entry SCHISTOSOMA_MANSONI = new Entry(null, "Schistosoma mansoni", 6183, null);
    public static final Entry SCHMIDTEA_MEDITERRANEA = new Entry(null, "Schmidtea mediterranea", 79327, null);
    public static final Entry CAPITELLA_TELETA = new Entry(null, "Capitella teleta", 283909, null);
    public static final Entry CEREBRATULUS_LACTEUS = new Entry(null, "Cerebratulus lacteus", 6221, null);
    public static final Entry HALIOTIS_RUFESCENS = new Entry(null, "Haliotis rufescens", 6454, null);
    public static final Entry LOTTIA_GIGANTEA = new Entry(null, "Lottia gigantea", 225164, null);
    public static final Entry DICTYOSTELIUM_DISCOIDEUM = new Entry(null, "Dictyostelium discoideum", 44689, null);
    public static final Entry CHLAMYDOMONAS_REINHARDTII = new Entry(null, "Chlamydomonas reinhardtii", 3055, null);
    public static final Entry PINUS_TAEDA = new Entry(null, "Pinus taeda", 3352, null);
    public static final Entry SELAGINELLA_MOELLENDORFFII = new Entry(null, "Selaginella moellendorffii", 88036, null);
    public static final Entry BRASSICA_NAPUS = new Entry(null, "Brassica napus", 3708, null);
    public static final Entry BRASSICA_OLERACEA = new Entry(null, "Brassica oleracea", 3712, null);
    public static final Entry BRASSICA_RAPA = new Entry(null, "Brassica rapa", 3711, null);
    public static final Entry CARICA_PAPAYA = new Entry(null, "Carica papaya", 3649, null);
    public static final Entry LOTUS_JAPONICUS = new Entry(null, "Lotus japonicus", 34305, null);
    public static final Entry VIGNA_UNGUICULATA = new Entry(null, "Vigna unguiculata", 3917, null);
    public static final Entry GOSSYPIUM_HERBACEUM = new Entry(null, "Gossypium herbaceum", 34274, null);
    public static final Entry GOSSYPIUM_HIRSUTUM = new Entry(null, "Gossypium hirsutum", 3635, null);
    public static final Entry GOSSYPIUM_RAIMONDII = new Entry(null, "Gossypium raimondii", 29730, null);
    public static final Entry POPULUS_TRICHOCARPA = new Entry(null, "Populus trichocarpa", 3694, null);
    public static final Entry SORGHUM_BICOLOR = new Entry(null, "Sorghum bicolor", 4558, null);
    public static final Entry SACCHARUM_OFFICINARUM = new Entry(null, "Saccharum officinarum", 4547, null);
    public static final Entry TRITICUM_AESTIVUM = new Entry(null, "Triticum aestivum", 4565, null);
    public static final Entry PHASEOLUS_VULGARIS = new Entry(null, "Phaseolus vulgaris", 3885, null);
    public static final Entry MALUS_DOMESTICA = new Entry(null, "Malus domestica", 3750, null);
    public static final Entry CAENORHABDITIS_REMANEI = new Entry(null, "Caenorhabditis remanei", 31234, null);
    public static final Entry PRISTIONCHUS_PACIFICUS = new Entry(null, "Pristionchus pacificus", 54126, null);
    public static final Entry BRACHYPODIUM_DISTACHYON = new Entry(null, "Brachypodium distachyon", 15368, null);
    public static final Entry AQUILEGIA_CAERULEA = new Entry(null, "Aquilegia caerulea", 218851, null);
    public static final Entry POPULUS_EUPHRATICA = new Entry(null, "Populus euphratica", 75702, null);
    public static final Entry CITRUS_SINENSIS = new Entry(null, "Citrus sinensis", 2711, null);
    public static final Entry CITRUS_CLEMENTINA = new Entry(null, "Citrus clementina", 85681, null);
    public static final Entry CITRUS_RETICULATA = new Entry(null, "Citrus reticulata", 85571, null);
    public static final Entry CITRUS_TRIFOLIATA = new Entry(null, "Citrus trifoliata", 37690, null);
    public static final Entry BRUGIA_MALAYI = new Entry(null, "Brugia malayi", 6279, null);
    public static final Entry ACYRTHOSIPHON_PISUM = new Entry(null, "Acyrthosiphon pisum", 7029, null);
    public static final Entry RICINUS_COMMUNIS = new Entry(null, "Ricinus communis", 3988, null);
    public static final Entry AEDES_AEGYPTI = new Entry(null, "Aedes aegypti", 7159, null);
    public static final Entry GOSSYPIUM_ARBOREUM = new Entry(null, "Gossypium arboreum", 29729, null);
    public static final Entry CULEX_QUINQUEFASCIATUS = new Entry(null, "Culex quinquefasciatus", 7176, null);
    public static final Entry ARABIDOPSIS_LYRATA = new Entry(null, "Arabidopsis lyrata", 59689, null);
    public static final Entry ECTOCARPUS_SILICULOSUS = new Entry(null, "Ectocarpus siliculosus", 2880, null);
    public static final Entry NASONIA_VITRIPENNIS = new Entry(null, "Nasonia vitripennis", 7425, null);
    public static final Entry ARACHIS_HYPOGAEA = new Entry(null, "Arachis hypogaea", 3818, null);
    public static final Entry GLYCINE_SOJA = new Entry(null, "Glycine soja", 3848, null);
    public static final Entry PICEA_ABIES = new Entry(null, "Picea abies", 3329, null);
    public static final Entry TRITICUM_TURGIDUM = new Entry(null, "Triticum turgidum", 4571, null);
    public static final Entry AEGILOPS_TAUSCHII = new Entry(null, "Aegilops tauschii", 37682, null);
    public static final Entry HORDEUM_VULGARE = new Entry(null, "Hordeum vulgare", 4513, null);
    public static final Entry FESTUCA_ARUNDINACEA = new Entry(null, "Festuca arundinacea", 4606, null);
    public static final Entry NASONIA_GIRAULTI = new Entry(null, "Nasonia giraulti", 7426, null);
    public static final Entry NASONIA_LONGICORNIS = new Entry(null, "Nasonia longicornis", 7427, null);
    public static final Entry STRIGAMIA_MARITIMA = new Entry(null, "Strigamia maritima", 126957, null);
    public static final Entry PETROMYZON_MARINUS = new Entry(null, "Petromyzon marinus", 7757, null);
    public static final Entry BRUGIA_PAHANGI = new Entry(null, "Brugia pahangi", 6280, null);
    public static final Entry THEOBROMA_CACAO = new Entry(null, "Theobroma cacao", 3641, null);
    public static final Entry XENOTURBELLA_BOCKI = new Entry(null, "Xenoturbella bocki", 242395, null);
    public static final Entry ECHINOCOCCUS_GRANULOSUS = new Entry(null, "Echinococcus granulosus", 6210, null);
    public static final Entry ECHINOCOCCUS_MULTILOCULARIS = new Entry(null, "Echinococcus multilocularis", 6211, null);
    public static final Entry HELICONIUS_MELPOMENE = new Entry(null, "Heliconius melpomene", 34740, null);
    public static final Entry REHMANNIA_GLUTINOSA = new Entry(null, "Rehmannia glutinosa", 99300, null);
    public static final Entry BRUGUIERA_GYMNORHIZA = new Entry(null, "Bruguiera gymnorhiza", 39984, null);
    public static final Entry BRUGUIERA_CYLINDRICA = new Entry(null, "Bruguiera cylindrica", 106616, null);
    public static final Entry GLOTTIDIA_PYRAMIDATA = new Entry(null, "Glottidia pyramidata", 34515, null);
    public static final Entry TEREBRATULINA_RETUSA = new Entry(null, "Terebratulina retusa", 7580, null);
    public static final Entry CUCUMIS_MELO = new Entry(null, "Cucumis melo", 3656, null);
    public static final Entry RHIPICEPHALUS_MICROPLUS = new Entry(null, "Rhipicephalus microplus", 6941, null);
    public static final Entry ASCARIS_SUUM = new Entry(null, "Ascaris suum", 6253, null);
    public static final Entry ANOLIS_CAROLINENSIS = new Entry(null, "Anolis carolinensis", 28377, null);
    public static final Entry ACACIA_MANGIUM = new Entry(null, "Acacia mangium", 224085, null);
    public static final Entry ACACIA_AURICULIFORMIS = new Entry(null, "Acacia auriculiformis", 205027, null);
    public static final Entry PHAEODACTYLUM_TRICORNUTUM = new Entry(null, "Phaeodactylum tricornutum", 2850, null);
    public static final Entry MACROPUS_EUGENII = new Entry(null, "Macropus eugenii", 9315, null);
    public static final Entry SALVIA_SCLAREA = new Entry(null, "Salvia sclarea", 38869, null);
    public static final Entry SARCOPHILUS_HARRISII = new Entry(null, "Sarcophilus harrisii", 9305, null);
    public static final Entry TETRANYCHUS_URTICAE = new Entry(null, "Tetranychus urticae", 32264, null);
    public static final Entry HAEMONCHUS_CONTORTUS = new Entry(null, "Haemonchus contortus", 6289, null);
    public static final Entry DIGITALIS_PURPUREA = new Entry(null, "Digitalis purpurea", 4164, null);
    public static final Entry NICOTIANA_TABACUM = new Entry(null, "Nicotiana tabacum", 4097, null);
    public static final Entry SOLANUM_TUBEROSUM = new Entry(null, "Solanum tuberosum", 4113, null);
    public static final Entry ELAEIS_GUINEENSIS = new Entry(null, "Elaeis guineensis", 51953, null);
    public static final Entry MANIHOT_ESCULENTA = new Entry(null, "Manihot esculenta", 3983, null);
    public static final Entry MANDUCA_SEXTA = new Entry(null, "Manduca sexta", 7130, null);
    public static final Entry CYNARA_CARDUNCULUS = new Entry(null, "Cynara cardunculus", 4265, null);
    public static final Entry HEVEA_BRASILIENSIS = new Entry(null, "Hevea brasiliensis", 3981, null);
    public static final Entry MARSUPENAEUS_JAPONICUS = new Entry(null, "Marsupenaeus japonicus", 27405, null);
    public static final Entry PINUS_DENSATA = new Entry(null, "Pinus densata", 190402, null);
    public static final Entry PARALICHTHYS_OLIVACEUS = new Entry(null, "Paralichthys olivaceus", 8255, null);
    public static final Entry HELIANTHUS_ANNUUS = new Entry(null, "Helianthus annuus", 4232, null);
    public static final Entry HELIANTHUS_CILIARIS = new Entry(null, "Helianthus ciliaris", 73280, null);
    public static final Entry HELIANTHUS_TUBEROSUS = new Entry(null, "Helianthus tuberosus", 4233, null);
    public static final Entry HELIANTHUS_EXILIS = new Entry(null, "Helianthus exilis", 400408, null);
    public static final Entry HELIANTHUS_ARGOPHYLLUS = new Entry(null, "Helianthus argophyllus", 73275, null);
    public static final Entry HELIANTHUS_PETIOLARIS = new Entry(null, "Helianthus petiolaris", 4234, null);
    public static final Entry HELIANTHUS_PARADOXUS = new Entry(null, "Helianthus paradoxus", 73304, null);
    public static final Entry HIPPOGLOSSUS_HIPPOGLOSSUS = new Entry(null, "Hippoglossus hippoglossus", 8267, null);
    public static final Entry CYPRINUS_CARPIO = new Entry(null, "Cyprinus carpio", 7962, null);
    public static final Entry CUNNINGHAMIA_LANCEOLATA = new Entry(null, "Cunninghamia lanceolata", 28977, null);
    public static final Entry LINUM_USITATISSIMUM = new Entry(null, "Linum usitatissimum", 4006, null);
    public static final Entry PANAX_GINSENG = new Entry(null, "Panax ginseng", 4054, null);
    public static final Entry PRUNUS_PERSICA = new Entry(null, "Prunus persica", 3760, null);
    public static final Entry ARTIBEUS_JAMAICENSIS = new Entry(null, "Artibeus jamaicensis", 9417, null);
    public static final Entry ICTALURUS_PUNCTATUS = new Entry(null, "Ictalurus punctatus", 7998, null);
    public static final Entry CAENORHABDITIS_BRENNERI = new Entry(null, "Caenorhabditis brenneri", 135651, null);
    public static final Entry SYCON_CILIATUM = new Entry(null, "Sycon ciliatum", 27933, null);
    public static final Entry LEUCOSOLENIA_COMPLICATA = new Entry(null, "Leucosolenia complicata", 433461, null);
    public static final Entry PANAGRELLUS_REDIVIVUS = new Entry(null, "Panagrellus redivivus", 6233, null);
    public static final Entry AVICENNIA_MARINA = new Entry(null, "Avicennia marina", 82927, null);
    public static final Entry BRANCHIOSTOMA_BELCHERI = new Entry(null, "Branchiostoma belcheri", 7741, null);
    public static final Entry PATIRIA_MINIATA = new Entry(null, "Patiria miniata", 46514, null);
    public static final Entry LYTECHINUS_VARIEGATUS = new Entry(null, "Lytechinus variegatus", 7654, null);
    public static final Entry SALMO_SALAR = new Entry(null, "Salmo salar", 8030, null);
    public static final Entry STRONGYLOIDES_RATTI = new Entry(null, "Strongyloides ratti", 34506, null);
    public static final Entry PLUTELLA_XYLOSTELLA = new Entry(null, "Plutella xylostella", 51655, null);
    public static final Entry AMBORELLA_TRICHOPODA = new Entry(null, "Amborella trichopoda", 13333, null);
    public static final Entry PHYTOPHTHORA_INFESTANS = new Entry(null, "Phytophthora infestans", 4787, null);
    public static final Entry PHYTOPHTHORA_SOJAE = new Entry(null, "Phytophthora sojae", 67593, null);
    public static final Entry PHYTOPHTHORA_RAMORUM = new Entry(null, "Phytophthora ramorum", 164328, null);
    public static final Entry EPTESICUS_FUSCUS = new Entry(null, "Eptesicus fuscus", 29078, null);
    public static final Entry GYRODACTYLUS_SALARIS = new Entry(null, "Gyrodactylus salaris", 37629, null);
    public static final Entry ORYCTOLAGUS_CUNICULUS = new Entry(null, "Oryctolagus cuniculus", 9986, null);
    public static final Entry OPHIOPHAGUS_HANNAH = new Entry(null, "Ophiophagus hannah", 8665, null);
    public static final Entry TUPAIA_CHINENSIS = new Entry(null, "Tupaia chinensis", 246437, null);
    public static final Entry PAPAVER_SOMNIFERUM = new Entry(null, "Papaver somniferum", 3469, null);
    public static final Entry PRUNUS_MUME = new Entry(null, "Prunus mume", 102107, null);
    public static final Entry MAYETIOLA_DESTRUCTOR = new Entry(null, "Mayetiola destructor", 39758, null);
    public static final Entry APHIS_GOSSYPII = new Entry(null, "Aphis gossypii", 80765, null);
    public static final Entry CHRYSEMYS_PICTA = new Entry(null, "Chrysemys picta", 8479, null);
    public static final Entry ALLIGATOR_MISSISSIPPIENSIS = new Entry(null, "Alligator mississippiensis", 8496, null);
    public static final Entry COLUMBA_LIVIA = new Entry(null, "Columba livia", 8932, null);
    public static final Entry PYTHON_BIVITTATUS = new Entry(null, "Python bivittatus", 176946, null);
    public static final Entry CALLORHINCHUS_MILII = new Entry(null, "Callorhinchus milii", 7868, null);
    public static final Entry SETARIA_ITALICA = new Entry(null, "Setaria italica", 4555, null);
    public static final Entry CALLITHRIX_JACCHUS = new Entry(null, "Callithrix jacchus", 9483, null);
    public static final Entry PTEROPUS_ALECTO = new Entry(null, "Pteropus alecto", 9402, null);
    public static final Entry HELIGMOSOMOIDES_POLYGYRUS = new Entry(null, "Heligmosomoides polygyrus", 6339, null);
    public static final Entry VRIESEA_CARINATA = new Entry(null, "Vriesea carinata", 294102, null);
    public static final Entry EUGENIA_UNIFLORA = new Entry(null, "Eugenia uniflora", 119951, null);
    public static final Entry CARASSIUS_AURATUS = new Entry(null, "Carassius auratus", 7957, null);
    public static final Entry ONCORHYNCHUS_MYKISS = new Entry(null, "Oncorhynchus mykiss", 8022, null);
    public static final Entry TRIOPS_CANCRIFORMIS = new Entry(null, "Triops cancriformis", 194544, null);
    public static final Entry SPODOPTERA_FRUGIPERDA = new Entry(null, "Spodoptera frugiperda", 7108, null);
    public static final Entry OREOCHROMIS_NILOTICUS = new Entry(null, "Oreochromis niloticus", 8128, null);
    public static final Entry ASTATOTILAPIA_BURTONI = new Entry(null, "Astatotilapia burtoni", 8153, null);
    public static final Entry METRIACLIMA_ZEBRA = new Entry(null, "Metriaclima zebra", 106582, null);
    public static final Entry NEOLAMPROLOGUS_BRICHARDI = new Entry(null, "Neolamprologus brichardi", 32507, null);
    public static final Entry PUNDAMILIA_NYEREREI = new Entry(null, "Pundamilia nyererei", 303518, null);
    public static final Entry SALICORNIA_EUROPAEA = new Entry(null, "Salicornia europaea", 206448, null);
    public static final Entry BUBALUS_BUBALIS = new Entry(null, "Bubalus bubalis", 89462, null);
    public static final Entry ELECTROPHORUS_ELECTRICUS = new Entry(null, "Electrophorus electricus", 8005, null);
    public static final Entry GADUS_MORHUA = new Entry(null, "Gadus morhua", 8049, null);
    public static final Entry FRAGARIA_VESCA = new Entry(null, "Fragaria vesca", 101020, null);
    public static final Entry CUCUMIS_SATIVUS = new Entry(null, "Cucumis sativus", 3659, null);
    public static final Entry FASCIOLA_HEPATICA = new Entry(null, "Fasciola hepatica", 6192, null);
    public static final Entry ANAS_PLATYRHYNCHOS = new Entry(null, "Anas platyrhynchos", 8839, null);
    public static final Entry BISTON_BETULARIA = new Entry(null, "Biston betularia", 82595, null);
    public static final Entry CAMELINA_SATIVA = new Entry(null, "Camelina sativa", 90675, null);
    public static final Entry BACTROCERA_DORSALIS = new Entry(null, "Bactrocera dorsalis", 27457, null);
    public static final Entry PAEONIA_LACTIFLORA = new Entry(null, "Paeonia lactiflora", 35924, null);
    public static final Entry DINOPONERA_QUADRICEPS = new Entry(null, "Dinoponera quadriceps", 609295, null);
    public static final Entry POLISTES_CANADENSIS = new Entry(null, "Polistes canadensis", 91411, null);
    public static final Entry MARCHANTIA_POLYMORPHA = new Entry(null, "Marchantia polymorpha", 3197, null);
    public static final Entry PARASTEATODA_TEPIDARIORUM = new Entry(null, "Parasteatoda tepidariorum", 114398, null);
    public static final Entry MESOCESTOIDES_CORTI = new Entry(null, "Mesocestoides corti", 53468, null);
    public static final Entry CAVIA_PORCELLUS = new Entry(null, "Cavia porcellus", 10141, null);
    public static final Entry DASYPUS_NOVEMCINCTUS = new Entry(null, "Dasypus novemcinctus", 9361, null);
    public static final Entry MELIBE_LEONINA = new Entry(null, "Melibe leonina", 76178, null);
    public static final Entry ECHINOPS_TELFAIRI = new Entry(null, "Echinops telfairi", 9371, null);
    public static final Entry SALVIA_MILTIORRHIZA = new Entry(null, "Salvia miltiorrhiza", 226208, null);
    public static final Entry MICROCEBUS_MURINUS = new Entry(null, "Microcebus murinus", 30608, null);
    public static final Entry DAUBENTONIA_MADAGASCARIENSIS = new Entry(null, "Daubentonia madagascariensis", 31869,
                                                                       null);
    public static final Entry NOMASCUS_LEUCOGENYS = new Entry(null, "Nomascus leucogenys", 61853, null);
    public static final Entry SAIMIRI_BOLIVIENSIS = new Entry(null, "Saimiri boliviensis", 39432, null);
    public static final Entry OTOLEMUR_GARNETTII = new Entry(null, "Otolemur garnettii", 30611, null);
    public static final Entry PAPIO_HAMADRYAS = new Entry(null, "Papio hamadryas", 9557, null);
    public static final Entry NOTHOBRANCHIUS_FURZERI = new Entry(null, "Nothobranchius furzeri", 105023, null);
    public static final Entry ASPARAGUS_OFFICINALIS = new Entry(null, "Asparagus officinalis", 4686, null);
    public static final Entry AIPTASIA_PALLIDA = new Entry(null, "Aiptasia pallida", 1720309, null);
    public static final Entry SYMBIODINIUM_MICROADRIATICUM = new Entry(null, "Symbiodinium microadriaticum", 2951,
                                                                       null);

    private static final Entry[] ENTRIES = new Entry[]{
            ARABIDOPSIS_THALIANA, BOMBYX_MORI, BOS_TAURUS, CAENORHABDITIS_ELEGANS, CANIS_FAMILIARIS, CAPRA_HIRCUS,
            CRICETULUS_GRISEUS, DANIO_RERIO, DROSOPHILA_MELANOGASTER, EPSTEIN_BARR_VIRUS, EQUUS_CABALLUS,
            GALLID_ALPHAHERPESVIRUS_2, GALLUS_GALLUS, GLYCINE_MAX, GORILLA_GORILLA, HOMO_SAPIENS, HUMAN_CYTOMEGALOVIRUS,
            KAPOSI_SARCOMA_ASSOCIATED_HERPESVIRUS, MACACA_MULATTA, MACACA_NEMESTRINA, MEDICAGO_TRUNCATULA, MUS_MUSCULUS,
            ORYZA_SATIVA, ORYZIAS_LATIPES, OVIS_ARIES, PAN_PANISCUS, PAN_TROGLODYTES, PHYSCOMITRELLA_PATENS,
            PONGO_PYGMAEUS, RATTUS_NORVEGICUS, SOLANUM_LYCOPERSICUM, SUS_SCROFA, TAENIOPYGIA_GUTTATA,
            VESICULAR_STOMATITIS_INDIANA_VIRUS, VITIS_VINIFERA, XENOPUS_LAEVIS, XENOPUS_TROPICALIS, ZEA_MAYS,
            AMPHIMEDON_QUEENSLANDICA, NEMATOSTELLA_VECTENSIS, HYDRA_MAGNIPAPILLATA, SACCOGLOSSUS_KOWALEVSKII,
            STRONGYLOCENTROTUS_PURPURATUS, CIONA_INTESTINALIS, CIONA_SAVIGNYI, OIKOPLEURA_DIOICA,
            BRANCHIOSTOMA_FLORIDAE, MONODELPHIS_DOMESTICA, ATELES_GEOFFROYI, LAGOTHRIX_LAGOTRICHA, SAGUINUS_LABIATUS,
            PYGATHRIX_BIETI, SYMPHALANGUS_SYNDACTYLUS, LEMUR_CATTA, ORNITHORHYNCHUS_ANATINUS, FUGU_RUBRIPES,
            TETRAODON_NIGROVIRIDIS, ANOPHELES_GAMBIAE, APIS_MELLIFERA, DROSOPHILA_ANANASSAE, DROSOPHILA_ERECTA,
            DROSOPHILA_GRIMSHAWI, DROSOPHILA_MOJAVENSIS, DROSOPHILA_PERSIMILIS, DROSOPHILA_PSEUDOOBSCURA,
            DROSOPHILA_SECHELLIA, DROSOPHILA_SIMULANS, DROSOPHILA_VIRILIS, DROSOPHILA_WILLISTONI, DROSOPHILA_YAKUBA,
            LOCUSTA_MIGRATORIA, TRIBOLIUM_CASTANEUM, DAPHNIA_PULEX, IXODES_SCAPULARIS, CAENORHABDITIS_BRIGGSAE,
            SCHISTOSOMA_JAPONICUM, SCHISTOSOMA_MANSONI, SCHMIDTEA_MEDITERRANEA, CAPITELLA_TELETA, CEREBRATULUS_LACTEUS,
            HALIOTIS_RUFESCENS, LOTTIA_GIGANTEA, DICTYOSTELIUM_DISCOIDEUM, CHLAMYDOMONAS_REINHARDTII, PINUS_TAEDA,
            SELAGINELLA_MOELLENDORFFII, BRASSICA_NAPUS, BRASSICA_OLERACEA, BRASSICA_RAPA, CARICA_PAPAYA,
            LOTUS_JAPONICUS, VIGNA_UNGUICULATA, GOSSYPIUM_HERBACEUM, GOSSYPIUM_HIRSUTUM, GOSSYPIUM_RAIMONDII,
            POPULUS_TRICHOCARPA, SORGHUM_BICOLOR, SACCHARUM_OFFICINARUM, TRITICUM_AESTIVUM, PHASEOLUS_VULGARIS,
            MALUS_DOMESTICA, CAENORHABDITIS_REMANEI, PRISTIONCHUS_PACIFICUS, BRACHYPODIUM_DISTACHYON,
            AQUILEGIA_CAERULEA, POPULUS_EUPHRATICA, CITRUS_SINENSIS, CITRUS_CLEMENTINA, CITRUS_RETICULATA,
            CITRUS_TRIFOLIATA, BRUGIA_MALAYI, ACYRTHOSIPHON_PISUM, RICINUS_COMMUNIS, AEDES_AEGYPTI, GOSSYPIUM_ARBOREUM,
            CULEX_QUINQUEFASCIATUS, ARABIDOPSIS_LYRATA, ECTOCARPUS_SILICULOSUS, NASONIA_VITRIPENNIS, ARACHIS_HYPOGAEA,
            GLYCINE_SOJA, PICEA_ABIES, TRITICUM_TURGIDUM, AEGILOPS_TAUSCHII, HORDEUM_VULGARE, FESTUCA_ARUNDINACEA,
            NASONIA_GIRAULTI, NASONIA_LONGICORNIS, STRIGAMIA_MARITIMA, PETROMYZON_MARINUS, BRUGIA_PAHANGI,
            THEOBROMA_CACAO, XENOTURBELLA_BOCKI, ECHINOCOCCUS_GRANULOSUS, ECHINOCOCCUS_MULTILOCULARIS,
            HELICONIUS_MELPOMENE, REHMANNIA_GLUTINOSA, BRUGUIERA_GYMNORHIZA, BRUGUIERA_CYLINDRICA, GLOTTIDIA_PYRAMIDATA,
            TEREBRATULINA_RETUSA, CUCUMIS_MELO, RHIPICEPHALUS_MICROPLUS, ASCARIS_SUUM, ANOLIS_CAROLINENSIS,
            ACACIA_MANGIUM, ACACIA_AURICULIFORMIS, PHAEODACTYLUM_TRICORNUTUM, MACROPUS_EUGENII, SALVIA_SCLAREA,
            SARCOPHILUS_HARRISII, TETRANYCHUS_URTICAE, HAEMONCHUS_CONTORTUS, DIGITALIS_PURPUREA, NICOTIANA_TABACUM,
            SOLANUM_TUBEROSUM, ELAEIS_GUINEENSIS, MANIHOT_ESCULENTA, MANDUCA_SEXTA, CYNARA_CARDUNCULUS,
            HEVEA_BRASILIENSIS, MARSUPENAEUS_JAPONICUS, PINUS_DENSATA, PARALICHTHYS_OLIVACEUS, HELIANTHUS_ANNUUS,
            HELIANTHUS_CILIARIS, HELIANTHUS_TUBEROSUS, HELIANTHUS_EXILIS, HELIANTHUS_ARGOPHYLLUS, HELIANTHUS_PETIOLARIS,
            HELIANTHUS_PARADOXUS, HIPPOGLOSSUS_HIPPOGLOSSUS, CYPRINUS_CARPIO, CUNNINGHAMIA_LANCEOLATA,
            LINUM_USITATISSIMUM, PANAX_GINSENG, PRUNUS_PERSICA, ARTIBEUS_JAMAICENSIS, ICTALURUS_PUNCTATUS,
            CAENORHABDITIS_BRENNERI, SYCON_CILIATUM, LEUCOSOLENIA_COMPLICATA, PANAGRELLUS_REDIVIVUS, AVICENNIA_MARINA,
            BRANCHIOSTOMA_BELCHERI, PATIRIA_MINIATA, LYTECHINUS_VARIEGATUS, SALMO_SALAR, STRONGYLOIDES_RATTI,
            PLUTELLA_XYLOSTELLA, AMBORELLA_TRICHOPODA, PHYTOPHTHORA_INFESTANS, PHYTOPHTHORA_SOJAE, PHYTOPHTHORA_RAMORUM,
            EPTESICUS_FUSCUS, GYRODACTYLUS_SALARIS, ORYCTOLAGUS_CUNICULUS, OPHIOPHAGUS_HANNAH, TUPAIA_CHINENSIS,
            PAPAVER_SOMNIFERUM, PRUNUS_MUME, MAYETIOLA_DESTRUCTOR, APHIS_GOSSYPII, CHRYSEMYS_PICTA,
            ALLIGATOR_MISSISSIPPIENSIS, COLUMBA_LIVIA, PYTHON_BIVITTATUS, CALLORHINCHUS_MILII, SETARIA_ITALICA,
            CALLITHRIX_JACCHUS, PTEROPUS_ALECTO, HELIGMOSOMOIDES_POLYGYRUS, VRIESEA_CARINATA, EUGENIA_UNIFLORA,
            CARASSIUS_AURATUS, ONCORHYNCHUS_MYKISS, TRIOPS_CANCRIFORMIS, SPODOPTERA_FRUGIPERDA, OREOCHROMIS_NILOTICUS,
            ASTATOTILAPIA_BURTONI, METRIACLIMA_ZEBRA, NEOLAMPROLOGUS_BRICHARDI, PUNDAMILIA_NYEREREI,
            SALICORNIA_EUROPAEA, BUBALUS_BUBALIS, ELECTROPHORUS_ELECTRICUS, GADUS_MORHUA, FRAGARIA_VESCA,
            CUCUMIS_SATIVUS, FASCIOLA_HEPATICA, ANAS_PLATYRHYNCHOS, BISTON_BETULARIA, CAMELINA_SATIVA,
            BACTROCERA_DORSALIS, PAEONIA_LACTIFLORA, DINOPONERA_QUADRICEPS, POLISTES_CANADENSIS, MARCHANTIA_POLYMORPHA,
            PARASTEATODA_TEPIDARIORUM, MESOCESTOIDES_CORTI, CAVIA_PORCELLUS, DASYPUS_NOVEMCINCTUS, MELIBE_LEONINA,
            ECHINOPS_TELFAIRI, SALVIA_MILTIORRHIZA, MICROCEBUS_MURINUS, DAUBENTONIA_MADAGASCARIENSIS,
            NOMASCUS_LEUCOGENYS, SAIMIRI_BOLIVIENSIS, OTOLEMUR_GARNETTII, PAPIO_HAMADRYAS, NOTHOBRANCHIUS_FURZERI,
            ASPARAGUS_OFFICINALIS, AIPTASIA_PALLIDA, SYMBIODINIUM_MICROADRIATICUM
    };

    private static final Map<String, Entry> scientificNameLookup = new HashMap<>();
    private static final Map<Integer, Entry> ncbiTaxIdLookup = new HashMap<>();

    static {
        for (final Entry entry : ENTRIES) {
            scientificNameLookup.put(entry.scientificName, entry);
            ncbiTaxIdLookup.put(entry.ncbiTaxId, entry);
        }
    }

    public static Entry getByScientificName(final String scientificName) {
        return scientificNameLookup.get(scientificName);
    }

    public static Entry getByNCBITaxId(final Integer ncbiTaxId) {
        return ncbiTaxIdLookup.get(ncbiTaxId);
    }

    public static class Entry {
        public final String keggGenomeAbbreviation;
        public final String scientificName;
        public final Integer ncbiTaxId;
        public final String ensemblGenomeAbbreviation;

        public Entry(final String keggGenomeAbbreviation, final String scientificName, final Integer ncbiTaxId,
                     final String ensemblGenomeAbbreviation) {
            this.keggGenomeAbbreviation = keggGenomeAbbreviation;
            this.scientificName = scientificName;
            this.ncbiTaxId = ncbiTaxId;
            this.ensemblGenomeAbbreviation = ensemblGenomeAbbreviation;
        }
    }
}
