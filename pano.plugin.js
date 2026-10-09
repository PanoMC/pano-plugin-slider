// Plugin-level options of the Pano plugin kit (@panomc/plugin-kit). The namespace is `slider`
// (the plugin id minus `pano-plugin-`); the only view file sits in src/theme, so none moved.
export default {
  viewDirs: ['src/theme'],
  styles: {
    // Slider is drawn from admin settings: the caption box style (colour, opacity, blur, gradient padding) comes from
    // getBoxStyle(), the title and subtitle colours from the settings, and the image keeps its 400 px inline height that the
    // mobile rule overrides with !important. Inline values keep winning over Bootstrap, which a layered class would not.
    styleAttrAllow: ['Slider'],
  },
};
