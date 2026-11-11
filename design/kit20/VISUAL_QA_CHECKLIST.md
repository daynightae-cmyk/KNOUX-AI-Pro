# KNOUX AI — Visual Completion QA Checklist

## Required evidence for each screen
- [ ] Exact reference ID and entry route documented
- [ ] Existing route/business logic preserved; no duplicate implementation
- [ ] Real runtime screenshot, same density/orientation compared to reference
- [ ] Content hierarchy / hero icon / background / cards / typography / gradients reviewed
- [ ] Buttons and navigation actionable, correct disabled/loading/error states
- [ ] Screen entry/exit and press microanimations recorded
- [ ] No hardcoded reference statistics shown as real results
- [ ] Arabic RTL / English LTR and responsive checks
- [ ] Accessibility / reduced-motion checks
- [ ] Correct API capability handling, permissions, and error recovery
- [ ] Test results with commands, SHA, and blockers

## Splash gates
- [ ] Authentic K contour (no replacement glyph)
- [ ] Top wedge written
- [ ] Diagonal and cyan leg drawn
- [ ] `knoux ai` appears progressively
- [ ] Smile drawn below text left-to-right
- [ ] One small endpoint sparkle
- [ ] Single click synchronized to end of smile, honors mute/DND/preferences
- [ ] Soft transition to Home, no white/black frame or duplicate logo
- [ ] Cold vs warm startup and deep-link behavior verified
- [ ] Reduced-motion fallback and no repetitive playback
- [ ] Actual splash recording and audio sync evidence

## Safe file operation gates
- [ ] User can preview exact accessible files and sizes
- [ ] Selected bytes sum correctly
- [ ] Protected or unknown paths deselected
- [ ] Canonical copy retained per duplicate group
- [ ] User confirms deletion with correct OS flow
- [ ] Bytes reclaimed verified after execution
- [ ] Denied/revoked permissions and partial failures handled
- [ ] No unsupported cross-app deletion claims

## Release/hand-off
- [ ] 20 named surfaces traced (plus existing App Manager overview)
- [ ] Existing tests not disabled or silently removed
- [ ] Build+install+launch+navigation verified
- [ ] No active agent overwritten
- [ ] SHA, local/remote main and CI status documented
- [ ] Remaining personalization/functional integrations clearly listed
