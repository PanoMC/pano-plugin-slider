import {PanoPlugin, viewComponent} from '@panomc/sdk';
import {derived} from 'svelte/store';
import {_ as i18n} from '@panomc/sdk/utils/language';

const pluginId = 'pano-plugin-slider';

// this is to render plugin translations
export const _ = derived(i18n, ($_fn) => {
  return (key, options) => $_fn(`plugins.${pluginId}.${key}`, options);
});

export default class PanoPluginSlider extends PanoPlugin {
  onLoad() {
    const pano = this.pano;

    console.log('Slider plugin enabled, environment:' + pano.isPanel);

    if (pano.isPanel) {
      pano.ui.page.register({
        path: '/view/slider',
        component: viewComponent(() => import('./panel/SliderPage.svelte')),
        systemLayout: 'ViewLayout',
        resetLayout: false,
        permission: `pano.plugin.${pluginId}.manage.slider`,
      });

      pano.ui.view.themes.editMenu(async (items) => {
        items.push({
          href: '/view/slider',
          text: `plugins.${pluginId}.pages.slider.title`,
          permission: `pano.plugin.${pluginId}.manage.slider`,
        });
        return items;
      });
    } else {
      const sliderComponent = viewComponent(() => import('./theme/Slider.svelte'));

      pano.ui.hook.register({
        name: 'page:home:top',
        component: sliderComponent,
      });

      pano.ui.hook.register({
        name: 'page:top',
        component: sliderComponent,
      });
    }
  }

  onContextUpdate(ctx) { }

  onUnload() { }
}
