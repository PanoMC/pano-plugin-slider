<style>
  /* Kept rules: the z-index of the wrapper, the image heights, the layout of the caption box and its fade-in animation.
     Gone: the round-dot restyle of Bootstrap's carousel indicators, the text shadows, the redundant animation-name rule and
     the z-index 20/25 pair of caption and controls (plugin.css is layered, so Bootstrap's own z-index: 1 on the controls
     would win and the caption would cover them). */
  .slider-slider__wrapper {
    position: relative;
    z-index: 10;
  }

  .slider-slider__image-container {
    position: relative;
    width: 100%;
    height: 400px;
  }

  .slider-slider__image {
    height: 400px;
    object-fit: cover;
  }

  /* Next to the sidebar (page:home:top): the carousel has the ratio of a 1x1 sidebar card and is pulled to the column edges
     by cancelling the grid gutter. */
  .slider-slider--home {
    --slider-gutter: calc(var(--pano-space, 1rem) * 1.5);
    margin-left: calc(var(--slider-gutter) * -0.5);
    margin-right: calc(var(--slider-gutter) * -0.5);
    width: calc(100% + var(--slider-gutter));
  }

  .slider-slider--home .slider-slider__carousel {
    aspect-ratio: 2.05 / 1;
  }

  /* Global top (page:top): full width, panoramic ratio. */
  .slider-slider--top .slider-slider__carousel {
    aspect-ratio: 3.5 / 1;
    margin-bottom: 1.5rem;
  }

  .slider-slider--home .slider-slider__carousel .carousel-inner,
  .slider-slider--home .slider-slider__carousel .carousel-item,
  .slider-slider--top .slider-slider__carousel .carousel-inner,
  .slider-slider--top .slider-slider__carousel .carousel-item,
  .slider-slider--home .slider-slider__image-container,
  .slider-slider--top .slider-slider__image-container,
  .slider-slider--home .slider-slider__image,
  .slider-slider--top .slider-slider__image {
    height: 100%;
  }

  /* Caption links sit on the image: keep them readable. */
  .slider-slider__caption-box a {
    color: var(--pano-color-secondary);
    text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
  }

  .slider-slider__caption-box a:hover {
    opacity: 0.8;
  }

  @media (max-width: 767.98px) {
    .slider-slider__image-container {
      height: 250px;
    }

    .slider-slider__image {
      height: 250px;
    }

    .slider-slider--home .slider-slider__image,
    .slider-slider--top .slider-slider__image {
      height: 100%;
    }
  }

  .slider-slider__overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: var(--slider-overlay-bg, linear-gradient(0deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0) 100%));
    pointer-events: none;
  }

  .slider-slider__caption-box {
    padding: 1.5rem 5% 2rem 5%;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    background: var(--slider-box-bg);
    backdrop-filter: var(--slider-box-blur);
    -webkit-backdrop-filter: var(--slider-box-blur);
    transition: all 0.3s ease;
  }

  .slider-slider__caption-box h2 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
  }

  .slider-slider__caption-box p {
    font-size: 1.1rem;
    margin-bottom: 1.25rem;
  }

  .slider-slider__caption {
    left: 0;
    right: 0;
    bottom: 0;
    padding: 0;
    margin: 0;
    text-align: left;
  }

  .slider-slider__animate-up {
    animation: slider-fade-in-up 0.8s ease backwards;
  }

  .slider-slider__delay-1 {
    animation-delay: 0.2s;
  }

  @keyframes slider-fade-in-up {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
</style>

{#if shouldRender && sliderItems.length > 0}
  <div class="slider-slider {isPageTop ? 'slider-slider--top container' : 'slider-slider--home'}">
    <div class="slider-slider__wrapper">
      <div
        id="panoMainSlider"
        bind:this={carouselElement}
        class="slider-slider__carousel carousel shadow-sm rounded-4 overflow-hidden"
        class:carousel-fade={settings.transition === 'fade'}>
        <!-- Indicators -->
        {#if settings.indicators && sliderItems.length > 1}
          <div class="carousel-indicators">
            {#each sliderItems as sliderItem, i}
              <button
                type="button"
                data-bs-target="#panoMainSlider"
                data-bs-slide-to={i}
                class:active={i === 0}
                aria-current={i === 0 ? 'true' : 'false'}
                aria-label={$_('components.slider.slide', { index: i + 1 })}></button>
            {/each}
          </div>
        {/if}

        <!-- Slides -->
        <div class="carousel-inner">
          {#each sliderItems as item, i}
            <div class="carousel-item" class:active={i === 0}>
              <div
                class="slider-slider__image-container"
                style={settings.captionStyle !== 'none' ? '--slider-overlay-bg: transparent;' : ''}>
                <img
                  src={item.imageUrl.startsWith('http') ? item.imageUrl : `${base}${item.imageUrl}`}
                  class="slider-slider__image d-block w-100 object-fit-cover"
                  alt={item.title} />
                <div class="slider-slider__overlay"></div>
              </div>
              {#if item.title || item.subtitle || item.linkUrl}
                <div class="slider-slider__caption carousel-caption d-md-block">
                  <div class="slider-slider__caption-box" style={getBoxStyle(settings)}>
                    {#if item.title}
                      {#if settings.titleTag === 'h1'}
                        <h1 class="slider-slider__title fw-bold slider-slider__animate-up" style="color: {settings.titleColor};">{item.title}</h1>
                      {:else if settings.titleTag === 'h3'}
                        <h3 class="slider-slider__title-2 fw-bold slider-slider__animate-up" style="color: {settings.titleColor};">{item.title}</h3>
                      {:else if settings.titleTag === 'h4'}
                        <h4 class="slider-slider__title-3 fw-bold slider-slider__animate-up" style="color: {settings.titleColor};">{item.title}</h4>
                      {:else}
                        <h2 class="slider-slider__title-4 fw-bold slider-slider__animate-up" style="color: {settings.titleColor};">{item.title}</h2>
                      {/if}
                    {/if}
                    {#if item.subtitle}
                      <p
                        class="opacity-75 slider-slider__animate-up slider-slider__delay-1"
                        style="color: {settings.subtitleColor};">
                        {item.subtitle}
                      </p>
                    {/if}
                    {#if item.linkUrl}
                      <a
                        href={item.linkUrl}
                        target={item.openInNewTab ? '_blank' : undefined}
                        rel={item.openInNewTab ? 'noopener noreferrer' : undefined}
                        class="link-secondary w-100 text-decoration-none">
                        {$_('pages.slider.table.view-details')} <i class="fas fa-external-link-square-alt ms-2"></i>
                      </a>
                    {/if}
                  </div>
                </div>
              {/if}
            </div>
          {/each}
        </div>

        <!-- Controls -->
        {#if settings.controls && sliderItems.length > 1}
          <button
            class="carousel-control-prev"
            type="button"
            data-bs-target="#panoMainSlider"
            data-bs-slide="prev">
            <span
              class="carousel-control-prev-icon rounded-circle bg-dark bg-opacity-25 p-3"
              aria-hidden="true"></span>
            <span class="visually-hidden">{$_('components.slider.previous')}</span>
          </button>
          <button
            class="carousel-control-next"
            type="button"
            data-bs-target="#panoMainSlider"
            data-bs-slide="next">
            <span
              class="carousel-control-next-icon rounded-circle bg-dark bg-opacity-25 p-3"
              aria-hidden="true"></span>
            <span class="visually-hidden">{$_('components.slider.next')}</span>
          </button>
        {/if}
      </div>
    </div>
  </div>
{/if}

<script module>
  export const view = { hook: ['page:home:top', 'page:top'] };
  import { api } from '@panomc/sdk/plugin-api';

  export async function load(event) {
    if (!event) return { sliderItems: [] };

    let output = {};

    try {
      const res = await api.get({
        path: '/items',
        request: event,
      });

      if (
        res.settings.renderHook !== event.hookName ||
        (res.settings.homepageOnly && event.url.pathname !== '/') ||
        (res.items ?? []).length === 0
      ) {
        output = { hookOptions: { invisible: true } };
      }

      output = {
        ...output,
        sliderItems: res.items || [],
        settings: res.settings || {
          renderHook: 'page:home:top',
          homepageOnly: true,
          titleColor: '#ffffff',
          subtitleColor: '#ffffff',
          captionBackground: '#000000',
          captionOpacity: 0.5,
          blurAmount: 8,
        },
      };
    } catch (err) {
      console.error('[Slider] Failed to fetch:', err);
      output = {
        ...output,
        sliderItems: [],
        settings: {
          renderHook: 'page:home:top',
          homepageOnly: true,
          titleColor: '#ffffff',
          subtitleColor: '#ffffff',
          captionBackground: '#000000',
          captionOpacity: 0.5,
          blurAmount: 8,
        },
      };
    }

    return output;
  }
</script>

<script>
  import { base, page } from '@panomc/sdk/svelte';
  import { derived } from 'svelte/store';
  import { _ as i18n } from '@panomc/sdk/utils/language';

  // plugin translations: `$_('key')` reads `plugins.pano-plugin-slider.key`
  const _ = derived(i18n, ($_fn) => (key, options) => $_fn(`plugins.pano-plugin-slider.${key}`, options));

  function hexToRgba(hex, opacity) {
    let c;
    if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
      c = hex.substring(1).split('');
      if (c.length == 3) {
        c = [c[0], c[0], c[1], c[1], c[2], c[2]];
      }
      c = '0x' + c.join('');
      return `rgba(${(c >> 16) & 255}, ${(c >> 8) & 255}, ${c & 255}, ${opacity})`;
    }
    return hex;
  }

  function getBoxStyle(settings) {
    if (settings.captionStyle === 'none') {
      return '--slider-box-bg: transparent; --slider-box-blur: none;';
    }
    const opacity = settings.captionOpacity || 0.5;
    const bg = hexToRgba(settings.captionBackground || '#000000', opacity);
    const blur = settings.captionStyle === 'glass' ? `blur(${settings.blurAmount || 5}px)` : 'none';

    if (settings.captionStyle === 'gradient') {
      const moreOpaqueBg = hexToRgba(settings.captionBackground || '#000000', Math.min(opacity * 1.8, 0.95));
      const midBg = hexToRgba(settings.captionBackground || '#000000', opacity);
      const transparentBg = hexToRgba(settings.captionBackground || '#000000', 0);
      return `--slider-box-bg: linear-gradient(to top, ${moreOpaqueBg} 0%, ${midBg} 40%, ${transparentBg} 100%); --slider-box-blur: ${blur}; padding-top: 10rem; padding-bottom: 2rem;`;
    }

    return `--slider-box-bg: ${bg}; --slider-box-blur: ${blur};`;
  }

  let { sliderItems = [], settings = {}, hookName } = $props();

  let shouldRender = $derived.by(() => {
    // If hook doesn't match, don't render
    if (hookName !== settings.renderHook) return false;

    // If homepageOnly is true, check if we are on homepage
    if (settings.homepageOnly) {
      const isHome = $page.url.pathname === '/' || $page.url.pathname === '';
      if (!isHome) return false;
    }

    return true;
  });

  let isPageTop = $derived(hookName === 'page:top');

  let carouselElement = $state();
  let carouselInstance = null;

  $effect(() => {
    if (!carouselElement || !shouldRender || sliderItems.length === 0) return;

    let retryCount = 0;
    const maxRetries = 20; // 2 seconds max

    const initCarousel = () => {
      if (window.bootstrap?.Carousel) {
        // Bootstrap animates the slide change only when the carousel has the `slide` class; it has no style of its own.
        carouselElement.classList.add('slide');
        carouselInstance = new window.bootstrap.Carousel(carouselElement, {
          interval: settings.autoSlide ? settings.interval : false,
          pause: settings.pauseOnHover ? 'hover' : false,
          wrap: settings.wrap,
          ride: settings.autoSlide ? 'carousel' : false,
        });

        if (settings.autoSlide) {
          carouselInstance.cycle();
        }
        return true;
      }
      return false;
    };

    if (!initCarousel()) {
      const interval = setInterval(() => {
        retryCount++;
        if (initCarousel() || retryCount >= maxRetries) {
          clearInterval(interval);
        }
      }, 100);

      return () => {
        clearInterval(interval);
        if (carouselInstance) {
          carouselInstance.dispose();
          carouselInstance = null;
        }
      };
    }

    return () => {
      if (carouselInstance) {
        carouselInstance.dispose();
        carouselInstance = null;
      }
    };
  });
</script>
