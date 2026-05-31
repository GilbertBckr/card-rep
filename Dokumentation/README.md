<img src="https://media.github.tools.sap/user/54420/files/8a0fe5cd-94d0-4bf2-8107-2c5994862f2d" width="100%" />

# SAP Typst Template for DHBW

This is a university report template written in [Typst](https://typst.app/),
based on the [vtgermany/LaTeX-Template-DHBW](https://github.wdf.sap.corp/vtgermany/LaTeX-Template-DHBW) for LaTeX. [Why should you use Typst over LaTeX?](#-why-typst)

## 🏃‍♂️ Getting Started

> [!TIP]
> If you run into any issues, check out the [troubleshooting guide](#-troubleshooting).

For the following setup guide, make sure you have installed [Visual Studio Code](https://code.visualstudio.com/).

1. Click on [![Generate](https://img.shields.io/badge/Generate_from_template-8A2BE2?logo=github)](https://github.tools.sap/vt-tools/vt-template-typst/generate) and give your repository a telling name (e. g. `pa-1`)
2. Clone your repository to your local machine: `git clone https://github.tools.sap/<you>/pa-1.git`
3. Open this directory using VSCode and install the recommended extensions
   - Tinymist (provides completions, preview and PDF-generation for your template)
   - LTeX+ (spell checker)
4. Open the `main.typ` file. To toggle the live-preview:
   - Press the `Typst Preview: Preview Opened File` button in the top right of your file\
   OR
   - Open the command palette (<kbd>CMD</kbd>+<kbd>Shift</kbd>+<kbd>P</kbd>) and select `Typst Preview: Preview Opened File`
5. The initial document will guide you through the rest of the setup process and show you all the different features of the template
6. To generate a PDF document:
   - Press the `Show the exported PDF` button in the top right of your file\
   OR
   - Open the command palette (<kbd>CMD</kbd>+<kbd>Shift</kbd>+<kbd>P</kbd>) and select `Typst: Show exported PDF`

**Additional information can be found in the [Typst@STAR introductory presentation](https://github.tools.sap/star-tools/typst-presentation) and the official [Typst documentation](https://typst.app/docs/)**.

## 💡 Feedback

If you have any idea on how to **improve the template**, please check out the
[development repository](https://github.tools.sap/star-tools/sap-dhbw-typst-dev).

## 😵‍💫 Troubleshooting 

>[!TIP]
>Don't hesitate to [open an issue](https://github.tools.sap/vt-tools/vt-template-typst/issues/new) or
[contact us directly](https://github.tools.sap/star-tools#contact) if you are puzzled on what is going on.

Sometimes, the Tinymist makes it a bit hard to diagnose the root cause of an issue.
Then, it can be helpful to use the `typst` program directly! 
To achieve this, open the terminal inside of your VSCode project and:

1. Download and install Typst: `brew install typst` (if you don't have `homebrew`, install it as shown [here](https://brew.sh/))
2. Open a new terminal window and run `typst compile main.typ`
3. Most of the time, the original error is at the top of the program's output
4. If this doesn't help you to figure out the issue, please [open an issue](https://github.tools.sap/vt-tools/vt-template-typst/issues/new) or
[contact us directly](https://github.tools.sap/star-tools#contact)

## 🌐 Web-IDE (typst.app)

> [!WARNING]
> SAP does not allow students to use web-hosted editors like [typst.app](https://typst.app/) to compose scientific papers.

## ❓ Why Typst?

> Typst was born out of our frustration with LaTeX. Not knowing what we were in for, we decided to take matters into our own hands and started building. -Typst

Typst is a replacement for LaTeX which was designed to be **as powerful as LaTeX while being much easier to learn and use**.
It has a much simpler syntax (similar to Markdown), _actual good error messages_ (looking at you, LaTeX)
and out-of-the-box bibliography features using `.bib` or `.yaml` files (see `example.yaml`) (again, looking at you, LaTeX).

## History and Future of this Template
This repository is based on the great work of Daniel Statzner and others on [vt-template-typst](https://github.tools.sap/I550629/vt-template-typst).


This repository was contrived as a continuation of Daniel's work, because we wanted this important template to be part of a bigger project, where it can be managed by multiple people actively using it.
For this, our choice fell on [STAR Tools](https://github.tools.sap/star-tools).

**The template lives from the contribution of its users. Finding bugs, adapting to new versions of dependencies, or starting a discussion about any change, are only three ways to contribute to this project. So, do not hesitate to start your [contribution](#-Contribute) now :)**

The goal of this template is to make it as easy as possible for you to write your thesis at SAP! That being said, there are some alternatives out there:

- [clean-dhbw](https://typst.app/universe/package/clean-dhbw/): The "official" template for Computer Science at DHBW Karlsruhe. We found this template to be pretty opinionated and not so keen on customization.
- [supercharged-dhbw](https://github.com/DannySeidel/typst-dhbw-template): The "unofficial" template for DHBW students. As of 16.07.2025, the last commit was more than half a year ago, so outdated package versions may lead to unwanted behavior with the latest version of typst. Especially for non-typst-savvy users, we do not recommend using a not actively maintained template!
- [typst-template-dhbw](https://github.tools.sap/I568996/typst-template-dhbw): Another fork of Daniel Statzner's repository - also tailored for SAP use.


In the future, we may want to publish a company-agnostic version of this template on [github.com](github.com) so that every student at DHBW can access and use this template, not only SAP students. But at the moment, we cannot give a schedule for this plan.