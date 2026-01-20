<style>
  .slider-wrapper {
    position: relative;
    z-index: 10;
  }

  .slider-image-container {
    position: relative;
    width: 100%;
    height: 400px;
  }

  .slider-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: var(--overlay-bg, linear-gradient(0deg, rgba(0, 0, 0, 0.6) 0%, rgba(0, 0, 0, 0) 100%));
    pointer-events: none;
  }

  .caption-box {
    padding: 1.5rem 5% 2rem 5%;
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    background: var(--box-bg);
    backdrop-filter: var(--box-blur);
    -webkit-backdrop-filter: var(--box-blur);
    transition: all 0.3s ease;
  }

  .caption-box h2 {
    font-size: 2rem;
    margin-bottom: 0.5rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }

  .caption-box p {
    font-size: 1.1rem;
    margin-bottom: 1.25rem;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  }

  .carousel-caption {
    left: 0;
    right: 0;
    bottom: 0;
    padding: 0;
    margin: 0;
    z-index: 20;
    text-align: left;
  }

  .animate-up {
    animation: fadeInUp 0.8s ease backwards;
  }

  .delay-1 {
    animation-delay: 0.2s;
  }

  .delay-2 {
    animation-delay: 0.4s;
  }

  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .carousel-item.active .animate-up {
    animation-name: fadeInUp;
  }

  :global(.carousel-indicators [data-bs-target]) {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin: 0 5px;
    border: 2px solid rgba(255, 255, 255, 0.5);
    background-color: transparent;
    transition: all 0.3s ease;
  }

  :global(.carousel-indicators .active) {
    background-color: #fff !important;
    width: 30px;
    border-radius: 5px;
  }

</style>

{#if shouldRender && sliderItems.length > 0}
  <div class:container={hookName === 'page:top'}>
    <div class="slider-wrapper">
      <div
        id="panoMainSlider"
        bind:this={carouselElement}
        class="carousel slide shadow-sm rounded-4 overflow-hidden"
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
                class="slider-image-container"
                style={settings.captionStyle !== 'none' ? '--overlay-bg: transparent;' : ''}>
                <img
                  src={item.imageUrl.startsWith('http') ? item.imageUrl : `${base}${item.imageUrl}`}
                  class="d-block w-100 object-fit-cover"
                  alt={item.title}
                  style="height: 400px;" />
                <div class="slider-overlay"></div>
              </div>
              {#if item.title || item.subtitle || item.linkUrl}
                <div class="carousel-caption d-md-block">
                  <div class="caption-box" style={getBoxStyle(settings)}>
                    {#if item.title}
                      <svelte:element
                        this={settings.titleTag || 'h2'}
                        class="fw-bold animate-up"
                        style="color: {settings.titleColor};">
                        {item.title}
                      </svelte:element>
                    {/if}
                    {#if item.subtitle}
                      <p
                        class="opacity-75 animate-up delay-1"
                        style="color: {settings.subtitleColor};">
                        {item.subtitle}
                      </p>
                    {/if}
                    {#if item.linkUrl}
                      <a
                        href={item.linkUrl}
                        target={item.openInNewTab ? '_blank' : undefined}
                        rel={item.openInNewTab ? 'noopener noreferrer' : undefined}
                        class="btn btn-primary btn-sm rounded-pill px-4 animate-up delay-2 shadow-sm">
                        {$_('pages.slider.table.view-details')}
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
    import ApiUtil from '@panomc/sdk/utils/api';

    export async function load(event) {
    if (!event) return { sliderItems: [] };

    let output = {};

    try {
      const res = await ApiUtil.get({
        path: '/api/slider/items',
        request: event,
      });

      if (
        res.settings.renderHook !== event.hookName ||
        (res.settings.homepageOnly && event.url.pathname !== '/') ||
        res.sliderItems.length === 0
      ) {
        output = { hookOptions: { invisible: true } };
      }

      output = {
        ...output,
        sliderItems: res.sliderItems || [],
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
  import { _ } from '../main';

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
      return '--box-bg: transparent; --box-blur: none;';
    }
    const opacity = settings.captionOpacity || 0.5;
    const bg = hexToRgba(settings.captionBackground || '#000000', opacity);
    const blur = settings.captionStyle === 'glass' ? `blur(${settings.blurAmount || 5}px)` : 'none';

    if (settings.captionStyle === 'gradient') {
      const moreOpaqueBg = hexToRgba(settings.captionBackground || '#000000', Math.min(opacity * 1.8, 0.95));
      const midBg = hexToRgba(settings.captionBackground || '#000000', opacity);
      const transparentBg = hexToRgba(settings.captionBackground || '#000000', 0);
      return `--box-bg: linear-gradient(to top, ${moreOpaqueBg} 0%, ${midBg} 40%, ${transparentBg} 100%); --box-blur: ${blur}; padding-top: 10rem; padding-bottom: 2rem;`;
    }

    return `--box-bg: ${bg}; --box-blur: ${blur};`;
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

  let carouselElement = $state();
  let carouselInstance = null;

  $effect(() => {
    if (!carouselElement || !shouldRender || sliderItems.length === 0) return;

    let retryCount = 0;
    const maxRetries = 20; // 2 seconds max

    const initCarousel = () => {
      if (window.bootstrap?.Carousel) {
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
