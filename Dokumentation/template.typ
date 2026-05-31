// LTeX: enabled=false

#import "@preview/glossarium:0.5.9": (
  gls, glspl, make-glossary, print-glossary, register-glossary,
)
#import "@preview/hydra:0.6.2": hydra
#import "@preview/codly-languages:0.1.10": *
#import "@preview/codly:1.3.0": *
#import "@preview/drafting:0.2.2": *
#import "utils.typ": program-specific

#let page_numbering = "1 / 1"
#let heading_numbering = "1.1.1"

#let __tpl_messages_array = (
  "submission_date": (
    "de": "Abgabedatum",
    "en": "Submission Date",
  ),
  "processing_period": (
    "de": "Bearbeitungszeitraum",
    "en": "Processing Period",
  ),
  "course": (
    "de": "Kurs",
    "en": "Course",
  ),
  "company": (
    "de": "Ausbildungsfirma",
    "en": "Training Company",
  ),
  "department": (
    "de": "Abteilung",
    "en": "Department",
  ),
  "supervisor_company": (
    "de": "Betreuer der Ausbildungsfirma",
    "en": "Supervisor of the Training Company",
  ),
  "supervisor_university": (
    "de": "Gutachter der Dualen Hochschule",
    "en": "Reviewer of the Duale Hochschule",
  ),
  "part_of": (
    "de": "Im Rahmen der Prüfung:",
    "en": "As part of the examination:",
  ),
  "at_the": (
    "de": "an der",
    "en": "at the",
  ),
  "university_pos": (
    "de": "Dualen Hochschule Baden-Württemberg Karlsruhe",
    "en": "Baden-Württemberg Cooperative State University Karlsruhe",
  ),
  "by": (
    "de": "von",
    "en": "by",
  ),
  "list_contents": (
    "de": "Inhaltsverzeichnis",
    "en": "Table of Contents",
  ),
  "list_abbreviations": (
    "de": "Abkürzungsverzeichnis",
    "en": "List of Abbreviations",
  ),
  "list_figures": (
    "de": "Abbildungsverzeichnis",
    "en": "Table of Figures",
  ),
  "list_tables": (
    "de": "Tabellenverzeichnis",
    "en": "Table Directory",
  ),
  "list_code": (
    "de": "Quellcodeverzeichnis",
    "en": "Source Code Directory",
  ),
  "list_bibliography": (
    "de": "Literaturverzeichnis",
    "en": "Bibliography",
  ),
  "appendix": (
    "de": "Anhang",
    "en": "Appendix",
  ),
  "list_appendix": (
    "de": "Anhangsverzeichnis",
    "en": "Index of Appendices",
  ),
  "list_of_notes": (
    "de": "Notizenverzeichnis",
    "en": "List of Notes",
  ),
)

#let __tpl_program_messages_array = (
  "identification_number": (
    "de": (
      "dhbw": "Matrikelnummer",
      "ihk": "Prüflingsnummer",
    ),
    "en": (
      "dhbw": "Matriculation Number",
      "ihk": "Examinee Number",
    ),
  ),
  "course_of_study": (
    "de": (
      "dhbw": "des Studienganges",
      "ihk": "des Ausbildungsberufes",
    ),
    "en": (
      "dhbw": "in",
      "ihk": "of the apprenticeship occupation",
    ),
  ),
)

#let __tpl_message(idx, lang) = __tpl_messages_array.at(idx).at(lang)

#let __tpl_program_message(idx, lang, program) = (
  __tpl_program_messages_array.at(idx).at(lang).at(program)
)

// usage: caption_with_source("text", [@source])
// prevents the using of the source in the outlines, to enable right sorting when using ieee for bibliography
#let in-outline = state("in-outline", false)
#let caption_with_source(caption_text, source) = context {
  if in-outline.at(here()) {
    caption_text
  } else {
    caption_text + " " + source
  }
}

#let get-separator(v) = if v != none { ", " + v } else { "" }

#let project(
  lang: "en",
  program: "dhbw",
  is_digital: true,
  confidentiality_clause: true,
  examination: none,
  study_field: none,
  title_long: none,
  title_short: none,
  thesis_type: none,
  authors: (
    (
      firstname: none,
      lastname: none,
      identification_number: none,
      course: none,
    ),
  ),
  signature_place: none,
  submission_date: none,
  processing_period: none,
  department: none,
  supervisor_company: none,
  supervisor_university: none,
  abstract: [],
  appendices: none,
  library_paths: (),
  acronyms: (),
  body,
) = {
  // page setup
  set document(title: title_long)
  set page(paper: "a4")

  // set text language (e. g. for smart quotes)
  set text(lang: lang)

  // justify content
  set par(justify: true)

  // font setup (LaTeX Look: 'New Computer Modern')
  set text(font: "New Computer Modern", size: 12pt)

  // heading setup
  set heading(numbering: heading_numbering)

  show heading.where(level: 1): it => {
    pagebreak(weak: true)
    it
  }

  show heading: it => {
    text(font: "Libertinus Serif", it)
    v(0.5cm)
  }


  show heading.where(level: 2): it => {
    v(weak: true, 1.2cm)
    it
  }

  // load additional syntaxes for code
  set raw(syntaxes: "syntax/cds.sublime-syntax")

  // fancy inline code
  // if you don't like them, just remove this section.
  show raw.where(block: false): box.with(
    fill: luma(240),
    inset: (x: 2pt, y: 0pt),
    outset: (y: 3pt),
    radius: 2pt,
  )

  // fancy code blocks
  // if you don't like them, just remove this section.
  show: codly-init.with()
  let languages-extended = codly-languages
  languages-extended.insert(
    "cds",
    (
      name: [CDS],
      color: rgb("#2599CD"),
      // styling from https://github.com/swaits/typst-collection/blob/b399080660c0566792cb3579ccf52ce7af9048a6/codly-languages/lib.typ#L16-L21
      icon: box(
        image("assets/cap-logo.svg", height: 0.9em),
        baseline: 0.05em,
        inset: 0pt,
        outset: 0pt,
      )
        + h(0.3em),
    ),
  )
  codly(
    languages: languages-extended,
    zebra-fill: none,
    display-icon: false,
    display-name: false,
    number-align: right + top,
  )

  show: make-glossary

  // Styled glossary references
  show ref: it => {
    let el = it.element
    if el != none and el.func() == figure and el.kind == "glossarium_entry" {
      // Make the glossarium entry references dark blue
      emph(it)
    } else if (el != none and el.func() == figure and el.kind == raw) {
      // Add styling for raw references (like references to headings)
      it
    } else if (el != none and el.func() == figure) {
      it
    } else {
      // Add styling for references to sources
      it
    }
  }

  // fancy inline links
  // if you don't like them, just remove this section.
  show link: it => {
    if type(it.dest) == str {
      set text(fill: gray.darken(80%))
      underline(
        stroke: (paint: gray, thickness: 0.5pt, dash: "densely-dashed"),
        offset: 4pt,
        it,
      )
    } else {
      it
    }
  }

  // Block quotes
  set quote(block: true)

  // Configure inline notes
  let caution-rect = rect.with(radius: 0.5em)
  set-margin-note-defaults(rect: caution-rect, fill: orange.lighten(80%))

  // Coversheet
  // Show notes before everything else, so you dont miss them
  context {
    // Check wether there are any notes in the document
    if (query(selector(<margin-note>).or(<inline-note>)).len() > 0) {
      set heading(numbering: none, outlined: false)
      note-outline(title: __tpl_message("list_of_notes", lang))
      pagebreak()
    }
  }


  // Allow code blocks to span multiple pages
  show figure.where(kind: raw): set block(breakable: true)

  let ident_label = __tpl_program_message(
    "identification_number",
    lang,
    program,
  )

  if authors.any(it => "course" in it) {
    ident_label += ", " + __tpl_message("course", lang)
  }

  // Coversheet
  grid(
    rows: (1fr, auto, 1fr),
    align: (_, row) => (center + top, center + top, center + bottom).at(row),
    // SAP and DHBW logo
    grid(
      columns: (1fr, 1fr),
      align(left)[
        #program-specific(program, (
          "ihk": image("assets/IHK-Logo.svg", height: 2.5cm),
          "dhbw": image("assets/DHBW-Logo.svg", height: 2.5cm),
        ))
      ],
    ),

    // Meta
    align(center)[
      #set par(justify: false)

      #text(20pt)[*#title_long*]

      #smallcaps(text(1.25em, weight: "semibold")[#thesis_type])

      #__tpl_message("part_of", lang)

      *#examination*

      #__tpl_program_message("course_of_study", lang, program) #study_field

      #if program != "ihk" {
        [#__tpl_message("at_the", lang) \
          #__tpl_message("university_pos", lang)]
      }

      #__tpl_message("by", lang)

      #for author in authors {
        [*#author.firstname #author.lastname*\ ]
      }
    ],

    // Advanced Meta
    align(center)[
      #table(
        columns: (1fr, 1fr),
        align: (right + top, left + top),
        stroke: none,
        ..if submission_date != none {
          ([*#__tpl_message("submission_date", lang)*], [#submission_date])
        },
        ..if processing_period != none {
          ([*#__tpl_message("processing_period", lang)*], [#processing_period])
        },
        [*#ident_label*],
        [#for author in authors {
            [ #author.identification_number#get-separator(author.at(
                "course",
                default: none,
              ))\ ]
          }
        ],

        [*#__tpl_message("company", lang)*],
        [
          SAP SE \
          Dietmar-Hopp-Allee 16 \
          69190 Walldorf, Deutschland
        ],

        ..if department != none {
          ([*#__tpl_message("department", lang)*], [#department])
        },
        ..if supervisor_company != none {
          (
            [*#__tpl_message("supervisor_company", lang)*],
            [#supervisor_company],
          )
        },
        ..if supervisor_university != none {
          (
            [*#__tpl_message("supervisor_university", lang)*],
            [#supervisor_university],
          )
        },
      )
    ],
  )
  pagebreak()

  // start page count on second page
  counter(page).update(1)
  set page(numbering: "I")

  // statutory declaration of originality
 /*  import "assets/declaration_of_originality.typ": declarationOfOriginalityWith
  declarationOfOriginalityWith(
    title_long: title_long,
    is_digital: is_digital,
    authors: authors,
    signature_place: signature_place,
    lang: lang,
    type: thesis_type,
    program: program,
  )
  pagebreak() */

  // restriction note
  if confidentiality_clause {
    import "assets/confidentiality_clause.typ": confidentialityClauseWith
    confidentialityClauseWith(lang: lang)
  }
/* 
  // abstracts
  for a in abstract {
    let (abstract_lang, abstract_lang_long, abstract_body) = a
    align(center + horizon)[
      #heading(outlined: false, numbering: none, [#text(
          0.85em,
          smallcaps[Abstract],
        )\ #text(
          0.75em,
          weight: "light",
          style: "italic",
          [\- #abstract_lang_long -],
        )])
      #text(lang: abstract_lang, abstract_body)
      #v(20%)
    ]
  } */

  // table of contents
  // show level 1 headings in outline in a fancier way, if not desired feel free to remove it
  [#show outline.entry.where(level: 1): it => {
      v(12pt, weak: true)
      strong(it)
    }
    #outline(
      title: [#__tpl_message("list_contents", lang)],
      depth: 3,
      indent: auto,
      target: selector(heading).before(
        <__appendix-start>,
      ),
    )
  ]

  // index of abbreviations
  if acronyms.len() > 0 {
    pagebreak()
    heading(outlined: false, numbering: none, __tpl_message(
      "list_abbreviations",
      lang,
    ))
    register-glossary(acronyms)
    print-glossary(acronyms, deduplicate-back-references: true)
  }

  // captions with caption_with_source shouldn't show source in outline
  show outline: it => {
    in-outline.update(true)
    it
    in-outline.update(false)
  }

  // only display certain outlines if elements for it exist
  context {
    // list of figures
    if query(figure.where(kind: image)).len() > 0 {
      pagebreak()
      heading(__tpl_message("list_figures", lang), numbering: none)
      outline(
        target: figure.where(kind: image).before(<__appendix-start>),
        title: none,
      )
    }

    // list of tables
    if query(figure.where(kind: table)).len() > 0 {
      pagebreak()
      heading(__tpl_message("list_tables", lang), numbering: none)
      outline(
        target: figure.where(kind: table).before(<__appendix-start>),
        title: none,
      )
    }

    // list of source code
    if query(figure.where(kind: raw)).len() > 0 {
      pagebreak()
      heading(__tpl_message("list_code", lang), numbering: none)
      outline(
        target: figure.where(kind: raw).before(<__appendix-start>),
        title: none,
      )
    }
  }

  // display header
  set page(header: {
    context {
      context {
        if here().page-numbering() == page_numbering {
          grid(
            columns: (auto, 1fr),
            align(left, text(title_short)),
            align(right, emph(hydra(1, display: (_, it) => {
              it.body
            }))),
          )
          line(length: 100%, stroke: (paint: gray))
        }
      }
    }
  })

  // display main document and reset page counter
  set page(
    numbering: page_numbering,
    footer: align(center)[
      #context counter(page).display() / #context {
        let end = query(<__thesis_end>).first()
        counter(page).at(end.location()).first()
      }
    ],
    margin: (top: 4cm, x: 2.5cm, bottom: 2.5cm),
  )
  counter(page).update(1)

  set par(leading: 0.9em)


  body
  [#[] <__thesis_end>]

  // display bibliography
  set page(numbering: "a", footer: auto)
  counter(page).update(1)

  bibliography(library_paths, title: [#__tpl_message(
    "list_bibliography",
    lang,
  )])

  // display appendix
  if appendices != none {
    set heading(level: 1, outlined: false)
    set page(numbering: "A", footer: auto)
    counter(page).update(1)
    counter(heading).update(0)

    heading(
      __tpl_message("list_appendix", lang),
      numbering: none,
      outlined: true,
    )

    outline(
      depth: 3,
      indent: auto,
      title: none,
      target: selector(heading).after(<__appendix-start>),
    )

    pagebreak(weak: true)
    [#[] <__appendix-start>]

    for appendix in appendices {
      let (appendix_heading, appendix_content) = appendix

      pagebreak()
      set heading(supplement: __tpl_message("appendix", lang), outlined: true)
      heading(
        [#__tpl_message("appendix", lang): #appendix_heading],
        outlined: true,
      )

      appendix_content
    }
  }
}
