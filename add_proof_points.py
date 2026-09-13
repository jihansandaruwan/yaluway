from docx import Document
from docx.shared import Pt
from docx.oxml.ns import qn
from docx.oxml import OxmlElement

path = r"D:\MAD\Assigment 01\Yaluway_Phase01_Proposal.docx"
doc = Document(path)


def set_run_font(run, size=11, bold=False):
    run.font.name = "Calibri"
    run._element.rPr.rFonts.set(qn("w:eastAsia"), "Calibri")
    run.font.size = Pt(size)
    run.bold = bold


def add_heading_after(ref_element, text, level=1):
    heading = doc.add_heading(text, level=level)
    # move heading before end by appending normally then relocating
    return heading


# Find insertion point: before "4. Target Audience" or after market gap section
insert_before = None
for p in doc.paragraphs:
    if p.text.strip().startswith("4. Target Audience"):
        insert_before = p._element
        break

# Build new content at end first, then move nodes before section 4
new_elements = []

h = doc.add_heading("3.2 Market Evidence & Supporting Statistics", level=2)
new_elements.append(h._element)
for run in h.runs:
    set_run_font(run, size=13, bold=True)

paras = [
    (
        "The need for Yaluway is supported by publicly available digital, labor, and marketplace "
        "statistics. These proof points strengthen the feasibility study and market-gap justification."
    ),
]

bullets = [
    "Internet adoption in Sri Lanka reached about 13.9 million users (59.7% of the population) by late 2025, showing a large mobile-ready audience for community apps (DataReportal Digital 2026: Sri Lanka).",
    "Sri Lanka had about 30.3 million cellular mobile connections in late 2025 (around 130% of population), indicating high mobile connectivity for Android-first solutions (DataReportal / GSMA Intelligence).",
    "According to TRCSL telecom statistics (Q4 2025), smartphones/tablets accounted for about 70.3% of end-user equipment (around 18.33 million devices), supporting a smartphone-based MVP.",
    "TRCSL also reports very high local use of Facebook (about 16.67 million) and WhatsApp (about 16.54 million) as of end-2025. This confirms that people already coordinate help and trade through social apps, but not through a structured hyperlocal community marketplace.",
    "Social media user identities in Sri Lanka were about 9.00 million in October 2025 (DataReportal). This shows strong digital social behavior, while also highlighting that social platforms are general-purpose and not specialized for trusted neighborhood services + marketplace use.",
    "Around 80.3% of Sri Lanka’s population lived in rural areas (DataReportal, late 2025). Hyperlocal tools can serve both urban apartment communities and village/town mutual-aid networks, not only city classifieds.",
    "ILO-linked data indicates informal employment around 66.8% of total employment in Sri Lanka (2019). This supports demand for a discovery channel for tutors, repair persons, cleaners, home cooks, and other micro-service providers currently found mainly by word-of-mouth.",
    "ikman reports itself as a leading Sri Lankan marketplace with over 4 million monthly users and hundreds of thousands of ads. This proves strong demand for digital buy/sell, but ikman is mainly island-wide classifieds—not a neighborhood community-help + services + borrow/donate platform (ikman / Digital Outlook Sri Lanka coverage).",
    "International hyperlocal successes show the model works when trust is local: Karrot (Korea) grew by restricting trade to nearby neighbors; AlloVoisins (France) combines local services and peer support at multi-million-user scale. These examples justify Yaluway’s neighborhood-first design.",
    "Industry guidance on local community marketplaces emphasizes that geographic proximity and community identity are stronger trust signals than open national marketplaces alone. Yaluway’s area-based help/services/marketplace design aligns with this principle.",
]

for text in paras:
    p = doc.add_paragraph()
    r = p.add_run(text)
    set_run_font(r)
    new_elements.append(p._element)

for text in bullets:
    p = doc.add_paragraph(text, style="List Bullet")
    if p.runs:
        set_run_font(p.runs[0])
    new_elements.append(p._element)

h2 = doc.add_heading("3.3 Competitor Gap Summary (Evidence-Based)", level=2)
new_elements.append(h2._element)
for run in h2.runs:
    set_run_font(run, size=13, bold=True)

gap_intro = doc.add_paragraph()
r = gap_intro.add_run(
    "Based on the above sources, existing platforms leave a clear gap that Yaluway targets:"
)
set_run_font(r)
new_elements.append(gap_intro._element)

gap_bullets = [
    "Social apps (Facebook/WhatsApp): high usage, low structure, weak search, weak service categorization, limited trust controls.",
    "Classified marketplaces (e.g., ikman): strong for ads and commerce, weak for community mutual aid, volunteering, borrow/donate culture, and neighborhood trust.",
    "Single-purpose donation/volunteer tools: useful for one need, not combined with local services and goods exchange.",
    "Yaluway gap position: one hyperlocal app for Community Help + Local Services + Marketplace (sell/donate/borrow).",
]
for text in gap_bullets:
    p = doc.add_paragraph(text, style="List Bullet")
    if p.runs:
        set_run_font(p.runs[0])
    new_elements.append(p._element)

# References section at end of document
ref_h = doc.add_heading("References (Web Sources Used for Proof Points)", level=1)
for run in ref_h.runs:
    set_run_font(run, size=16, bold=True)

refs = [
    "DataReportal (2026). Digital 2026: Sri Lanka. https://datareportal.com/reports/digital-2026-sri-lanka",
    "DataReportal (2024). Digital 2024: Sri Lanka. https://datareportal.com/reports/digital-2024-sri-lanka",
    "Telecommunications Regulatory Commission of Sri Lanka (TRCSL). Telecom Statistics of Sri Lanka – Q4 2025. https://www.trc.gov.lk/",
    "International Labour Organization / TheGlobalEconomy.com. Sri Lanka Informal Employment (latest reported value 66.8% in 2019). https://www.theglobaleconomy.com/Sri-Lanka/informal_employment/",
    "Newswire.lk / Digital Outlook Sri Lanka 2023 coverage on ikman rankings and scale (4M+ monthly users). https://www.newswire.lk/business/ikman-secures-4-top-rankings-on-digital-outlook-sri-lanka-2023-the-annual-market-insight-report/",
    "IDC via TelecomLead / Lanka Business Online (2024). Sri Lanka smartphone market growth report. https://telecomlead.com/telecom-statistics/sri-lanka-smartphone-market-surges-218-in-q2-2024-idc-118376",
    "Seoulz (2026). Korea Resale Economy / Karrot hyperlocal trust model. https://www.seoulz.com/korea-resale-economy-2026/",
    "Didit case study on AlloVoisins (France local services marketplace). https://didit.me/blog/allovoisins-success-story/",
    "LOW/CODE (2026). Local Community Marketplace Guide (trust and hyperlocal design principles). https://www.lowcode.agency/blog/how-to-build-a-local-community-marketplace",
]
for text in refs:
    p = doc.add_paragraph(text, style="List Number")
    if p.runs:
        set_run_font(p.runs[0], size=10)

# Move section 3.2/3.3 blocks before section 4 if found
if insert_before is not None:
    parent = insert_before.getparent()
    for el in new_elements:
        parent.remove(el)
        insert_before.addprevious(el)

out = r"D:\MAD\Assigment 01\Yaluway_Phase01_Proposal_with_Evidence.docx"
try:
    doc.save(path)
    print("Updated:", path)
except PermissionError:
    doc.save(out)
    print("Original file locked. Saved as:", out)
print("Inserted evidence section + references")
