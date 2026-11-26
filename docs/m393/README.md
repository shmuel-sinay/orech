# docs/m393: תיעוד מלא של עבודת M393

סדר קריאה לסשן חדש:
1. `HANDOFF_M393.md`: כללי ברזל ("אל תעשה X אלא אם Y"), התאוששות, מה לא נבדק, משימות פתוחות.
2. `BUILD_AND_DEPLOY_COMMANDS.md`: הפקודות המדויקות לבנייה, פריסה ושחזור.
3. `CHANGELOG_M393.md`, `REPORT_M393.md`, `SESSION_2026-11-22_FULL.md`: מה נעשה, מה נבדק.
4. `PROMPT_NEXT_SESSION.md`: פרומפט המשך מוכן להדבקה.

תיקיות: `deploy_scripts/` (כל סקריפטי הפריסה והשירותים, כולל יומנים), `build_inputs/` (רשימות מפתחות XML וכלי boot).
קוד: `tools/dex_edit`, `tools/arsc_xml_edit`, `tools/overlays`, `tools/m393_device`, `tools/mockloc`, `tools/boot_unlock`, `seller/`, `app/`.
קבצי firmware ובינאריים שנבנו מהם אינם בריפו (ראה סעיף 10 ב-`BUILD_AND_DEPLOY_COMMANDS.md`).