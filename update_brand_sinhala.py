from docx import Document
from docx.shared import Pt
from docx.oxml.ns import qn
import glob
import os

files = [
    r"D:\MAD\Assigment 01\Yaluway_Phase01_Proposal_with_Evidence.docx",
    r"D:\MAD\Assigment 01\Yaluway_Phase01_Proposal.docx",
]


def set_run_font(run, size=11, bold=False):
    run.font.name = "Calibri"
    run._element.rPr.rFonts.set(qn("w:eastAsia"), "Calibri")
    run.font.size = Pt(size)
    run.bold = bold


for path in files:
    if not os.path.exists(path):
        continue
    doc = Document(path)
    changed = 0
    for p in doc.paragraphs:
        full = p.text
        # Update brand meaning / sinhala notes
        if "Brand meaning:" in full or "Sinhala-English brand" in full or "Yalu" in full and "friend" in full.lower():
            for r in p.runs:
                t = r.text
                if not t:
                    continue
                nt = t
                nt = nt.replace("Yaluway", "යාළුway (Yaluway)")
                # avoid double replace loops on already updated
                if "යාළුway (යාළුway" in nt:
                    nt = t
                if "Sinhala-English brand: Yalu = friend" in nt:
                    nt = (
                        "Sinhala-English brand: යාළුway "
                        "(යාළු = friend + way = path)"
                    )
                if "Brand meaning:" in nt and "Yalu" in nt:
                    nt = (
                        'Brand meaning: "යාළු" (Sinhala for friend/yaluwa) + "way" (path). '
                        "Correct brand form: යාළුway (English form: Yaluway). "
                        "Together it means a friendly local path to get help, offer services, "
                        "and trade with people nearby. "
                        'Tagline: "Help nearby. Trade nearby. Trust nearby." '
                        "යාළුway aims to become the go-to mobile hub for neighborhood support and "
                        "small-scale local exchange, combining mutual aid with a structured local marketplace."
                    )
                if nt != t:
                    r.text = nt
                    changed += 1

        # Cover title if plain Yaluway - add Sinhala form in nearby subtitle only
        if p.text.strip() == "Yaluway":
            for r in p.runs:
                r.text = ""
            if p.runs:
                p.runs[0].text = "යාළුway"
                set_run_font(p.runs[0], size=28, bold=True)
                changed += 1

    out = path.replace(".docx", "_YaluSinhala.docx")
    try:
        doc.save(path)
        print("Updated:", path, "changes:", changed)
    except PermissionError:
        doc.save(out)
        print("Locked, saved:", out, "changes:", changed)
