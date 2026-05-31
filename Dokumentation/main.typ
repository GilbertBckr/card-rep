// LTeX: enabled=false
#import "template.typ": caption_with_source, project

#show: project.with(
  lang: "de",
  program: "dhbw",
  is_digital: true,
  confidentiality_clause: false,
  examination: "Bachelor of Science (B. Sc.)",
  study_field: "Informatik",

  title_long: "Programmentwurf: Card Repetition (card-rep)",
  title_short: "Programmentwurf Card Repetition",
  thesis_type: "Programmentwurf",

  authors: (
    (
      firstname: "[Vorname]",
      lastname: "[Name]",
      identification_number: "[MNR]",
      course: "TINF23B2",
    ),
  ),
  signature_place: "Karlsruhe",
  submission_date: "[DATUM]",
  processing_period: "[Bearbeitungszeitraum]",
  supervisor_university: "[Gutachter]",

  abstract: (),

  library_paths: "library.bib",

  acronyms: (
    (
      key: "SRP",
      short: "SRP",
      long: "Single Responsibility Principle",
    ),
    (
      key: "OCP",
      short: "OCP",
      long: "Open-Closed Principle",
    ),
    (
      key: "DIP",
      short: "DIP",
      long: "Dependency Inversion Principle",
    ),
    (
      key: "LSP",
      short: "LSP",
      long: "Liskov Substitution Principle",
    ),
    (
      key: "ISP",
      short: "ISP",
      long: "Interface Segregation Principle",
    ),
    (
      key: "DDD",
      short: "DDD",
      long: "Domain Driven Design",
    ),
    (
      key: "DRY",
      short: "DRY",
      long: "Don't Repeat Yourself",
    ),
    (
      key: "GRASP",
      short: "GRASP",
      long: "General Responsibility Assignment Software Patterns",
    ),
  ),
)

#include "chapters/kapitel1.typ"
#include "chapters/kapitel2.typ"
#include "chapters/kapitel3.typ"
#include "chapters/kapitel4.typ"
#include "chapters/kapitel5.typ"
#include "chapters/kapitel6.typ"
#include "chapters/kapitel7.typ"
#include "chapters/kapitel8.typ"
