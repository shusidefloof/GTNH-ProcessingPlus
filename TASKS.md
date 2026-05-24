GT:NH Processing+ — Multiblock Task List


════════════════════════════════════════
BOF — Basic Oxygen Furnace
════════════════════════════════════════

☐ Controller craft recipe
☐ Casing craft recipe verification (uses BorosilicateGlass — confirm it works)
☐ Slag byproduct output on all 3 modes — slag could feed a slag processing chain
☐ Alloy steel modes (chromium, manganese, vanadium steel) for higher-tier structural needs
☐ Phosphorus removal mode (real BOF does dephosphorization)
☐ Parallels
☐ Tooltip: tier listed as LV in design doc, code is HV — reconcile


════════════════════════════════════════
CSC — Cryogenic Separation Column
════════════════════════════════════════

☐ Noble gas extraction mode (circuit 3) — Neon/Krypton/Xenon for SPC tier gating
☐ Hydrogen liquefaction mode (circuit 4)
☐ Controller craft recipe
☐ Tooltip update to reflect all current modes


════════════════════════════════════════
SPC — Spectral Photolithography Chamber
════════════════════════════════════════

☐ Noble gas per-tier inputs on engraving recipes
☐ Controller craft recipe
☐ Scorched board as failure/waste output
☐ UEV+ board tiers (Exotic, Cosmic, Transcendent) — deferred but tracked


════════════════════════════════════════
CRV — Ceramic Reaction Vessel
════════════════════════════════════════

☐ Controller craft recipe
☐ Inner hBN ceramic block craft recipe (how does the player make the inner casing?)
☐ More amorphous metal recipes — design doc hints at new MABS exotic alloys
☐ SiC sintering move here from generic maps (thematically a ceramic reaction)
☐ Exotic molten metal containment recipes (design doc: only multiblock that can safely contain certain exotic molten metal mixtures)
☐ Parallels


════════════════════════════════════════
HTRF — High Temperature Reaction Furnace
════════════════════════════════════════

☐ Controller craft recipe
☐ SiC casing craft recipe (gates the machine — confirm recipe exists)
☐ More 2000K+ reactions: ZrO2 synthesis, refractory carbides, boride ceramics
☐ Verify SiC sintering recipes are actually registered to this machine
☐ Perfect OC support
☐ Parallels


════════════════════════════════════════
HPSF — High Pressure Sintering Furnace
════════════════════════════════════════

☐ Controller craft recipe
☐ Casing craft recipe
☐ SiC sintering recipes (design doc says serves SiC + hBN)
☐ Continuous inert gas requirement (design doc says required — not enforced in code yet)
☐ More sintering targets: alumina ceramics, tungsten carbide
☐ Parallels


════════════════════════════════════════
DAF — Dual Atmosphere Furnace
════════════════════════════════════════

☐ Controller craft recipe
☐ Casing craft recipe
☐ Screwdriver mode-switching implementation (design doc says switch with screwdriver)
☐ Carbon fiber pitch stabilization route recipes
☐ More dual-atmosphere uses: metal annealing under mixed atmospheres
☐ Nitrogen/Argon atmosphere verification — should consume CSC output


════════════════════════════════════════
PCV — Polycondensation Vessel
════════════════════════════════════════

☐ Controller craft recipe
☐ Casing craft recipe
☐ Nylon-6,6 polycondensation step moved here from generic LCR
☐ Kapton polymerization step moved here
☐ Vacuum mode vs pressure mode (two circuit variants)
☐ Batch mode (long polymer runs)
☐ Water/methanol byproduct recovery loop (condensation reaction byproducts)


════════════════════════════════════════
PFC — Precision Film Caster   *** NO RECIPES ***
════════════════════════════════════════

☐ Controller craft recipe
☐ Casing craft recipe
☐ Kapton film casting recipe (step 6 — currently in generic map)
☐ Kapton imidization recipe (thermal cure mode — design doc says two modes: casting + imidization)
☐ PLA membrane casting recipe
☐ Aerogel film/panel forming recipe
☐ Photoresist spin-coating step for SPC prep
☐ Circuit mode switching (casting mode vs imidization mode)
☐ N2 blanket atmosphere input (currently Kapton step 6 uses N2 in generic map — move here)


════════════════════════════════════════
AAR — Ammonia Atmosphere Reactor
════════════════════════════════════════

☐ Controller craft recipe
☐ Casing craft recipe
☐ Continuous ammonia supply check (design doc: recipe fails if ammonia supply drops — not enforced yet)
☐ Additional nitride synthesis targets beyond hBN (TiN, Si3N4, AlN)
☐ Nitrogen fixation route recipes
☐ Ammonia recovery output on completed recipes
☐ Parallels


════════════════════════════════════════
SCD — Supercritical Dryer
════════════════════════════════════════

☐ Controller craft recipe
☐ Casing craft recipe
☐ LiquidCO2 supercritical drying upgrade for aerogel (uses CSC CO2 mode output)
☐ Supercritical CO2 extraction recipes (pharmaceutical/resin extraction)
☐ Carbon aerogel variant (design doc v1.1 mentions carbon aerogel)
☐ Ethanol recovery loop verification — design doc says it is built in, confirm recipes match


════════════════════════════════════════
CIDC — Controlled Isotopic Doping Chamber   *** NOT IMPLEMENTED ***
════════════════════════════════════════

☐ MTE class + registration (ID 31511)
☐ Structure design and casing block
☐ Casing craft recipe
☐ Controller craft recipe
☐ UV photoresist RE-doping recipe: Tin Oxo Cluster + 4 RE compounds -> RE-Doped Matrix
☐ Recipe map registration
☐ Tooltip


════════════════════════════════════════
HPR — Hybrid Phase Reactor   *** NOT IMPLEMENTED ***
════════════════════════════════════════

☐ MTE class + registration (ID 31512)
☐ Structure design and casing block (handles biological + inorganic simultaneously)
☐ Casing craft recipe
☐ Controller craft recipe
☐ UHV photoresist recipe: Radox Polymer + Xenoxene -> Radox-Xenoxene Matrix
☐ Bio-safety mechanic (should it consume sterilization fluid?)
☐ Recipe map registration


════════════════════════════════════════
SPU — Subatomic Patterning Unit   *** NOT IMPLEMENTED ***
════════════════════════════════════════

☐ MTE class + registration (ID 31513)
☐ Structure design (appears 3 times in UIV chain — automation challenge by design)
☐ Casing craft recipe (likely requires exotic/UIV materials)
☐ Controller craft recipe
☐ UIV photoresist recipes: 3 separate steps all going through this machine
☐ Decide how the automation challenge is enforced mechanically
☐ Recipe map registration


════════════════════════════════════════
CRC — Catalytic Reaction Chamber   *** NOT IMPLEMENTED ***
════════════════════════════════════════

☐ MTE class + registration (ID 31514)
☐ Structure design — needs CF composite casings (gates on carbon fiber chain)
☐ CF composite casing craft recipe
☐ Controller craft recipe
☐ Aerogel catalyst support consumption mechanic (continuous input)
☐ Advanced chemistry recipes gated behind catalytic environment tag
☐ Recipe map registration


════════════════════════════════════════
PRIORITY NOTES
════════════════════════════════════════

1. PFC needs recipes before anything else — implemented but completely unused
2. CIDC blocks the UV photoresist chain from being completable in-game
3. HPR blocks UHV photoresist
4. Controller + casing recipes missing across nearly every machine — players cannot build most of these
