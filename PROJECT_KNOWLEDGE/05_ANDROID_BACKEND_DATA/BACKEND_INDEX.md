# KNOUX AI Pro — Backend / Android Data Index

دي خريطة معرفة للخدمات الحقيقية ومصادر بياناتها، **مش حالة تنفيذ ناجحة**.

## من المشروع الحالي مباشرة (الأولوية)

- [Sourcebook الخاص بـPro](../../docs/KNOUX_AI_PRO_BACKEND_SOURCEBOOK_20261115.docx) — ملف عمل حالي غير متتبع وقت الجرد؛ راجع محتواه الحي قبل استخدامه.
- [حدود البيانات الفعلية](../../docs/architecture/REAL_DATA_AND_BOUNDARIES.md).
- [قيود Android/Google Play](../../docs/policies/ANDROID_PLAY_CONSTRAINTS_2026_11_10.md).
- [Database](../../database/) — افحص مخططات SQL/Room الفعلية ولا تعتمد على تصميم قديم فقط.
- [عقود الخدمات العشر](../../prompts/phase2/) — متطلبات وقرارات سابقة.
- [الأدلة](../../qa-evidence/) — سجلات اختبار، وليست ضمانًا آليًا.

## سياق تاريخي مقارن

- [M00–M15 historical master](../08_RELATED_HISTORY_NOT_CANONICAL/HistoricalStandardSources/03_ORIGINAL_SPECS/MOBILE_SUPREME_M00_M15_E2E_MASTER.md).
- [Project historical source](../08_RELATED_HISTORY_NOT_CANONICAL/HistoricalStandardSources/02_CANONICAL/KNOUX_AI_LIVING_SOURCE_OF_TRUTH.md).

استخدم Android MediaStore/SAF/Room/PackageManager/Battery APIs حسب الصلاحيات الحقيقية. عشان تثبت وظيفة، نفذ action مُصرّحًا به ثم re-query وRoom/tests/device؛ لا تعتمد على واجهة أو رسالة Agent.
