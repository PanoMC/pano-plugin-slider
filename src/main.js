import {PanoPlugin, viewComponent} from '@panomc/sdk';
import {derived} from 'svelte/store';
import {_ as i18n} from '@panomc/sdk/utils/language';
import { showToast } from '@panomc/sdk/toasts';

const pluginId = 'pano-plugin-slider';

// this is to render plugin translations
export const _ = derived(i18n, ($_fn) => {
  return (key, options) => $_fn(`plugins.${pluginId}.${key}`, options);
});

// Success/failure colouring for this plugin's toasts, matching the panel. showToast from
// @panomc/sdk/toasts is the host panel's ToastContainer `show`, whose signature is
// (text, params, toastComponent, options): passing undefined for toastComponent keeps the
// host's DefaultToast, and options.variant maps to Bootstrap's text-success / text-danger.
// These live here rather than in @panomc/sdk/toasts because this plugin is pinned to
// @panomc/sdk 1.0.0-dev.39, which predates the variants; they can be dropped for a direct
// SDK import once that pin moves. On an older panel build the extra argument is ignored and
// the toast renders neutral, so this degrades instead of breaking.
export function showSuccessToast(text, params = {}) {
  return showToast(text, params, undefined, { variant: 'success' });
}

export function showErrorToast(text, params = {}) {
  return showToast(text, params, undefined, { variant: 'danger' });
}

export default class PanoPluginSlider extends PanoPlugin {
  onLoad() {
    const pano = this.pano;

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
