import { PanoPlugin, viewComponent } from "@panomc/sdk";
import { derived } from "svelte/store";
import { _ as i18n } from "@panomc/sdk/utils/language";

const pluginId = "pano-plugin-slider"

// this is to render plugin translations
export const _ = derived(i18n, ($_fn) => {
  return (key, options) => $_fn(`plugins.${pluginId}.${key}`, options);
});

export default class PanoPluginSlider extends PanoPlugin {
  onLoad() {
    const pano = this.pano

    console.log("Hello world! It is enabled, environment:" + pano.isPanel)

    if (pano.isPanel) {
      pano.ui.page.register({
        path: "/view/slider",
        component: viewComponent(() => import("./panel/SliderPage.svelte")),
        systemLayout: "ViewLayout",
        resetLayout: false,
      });

      pano.ui.view.themes.editMenu(async (items) => {
        items.push({
          href: '/view/slider',
          text: 'plugins.pano-plugin-slider.pages.slider.title',
        });
        return items;
      });
    } else {
      // here you can write your theme-related codes
    }
  }

  onContextUpdate(ctx) {
  }

  onUnload() {
  }
}