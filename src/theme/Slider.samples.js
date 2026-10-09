// Sample data of Slider for the view catalogue (doc 02 section 7). Pure data: import only view helpers and
// relative .js fixtures.
/** @type {string[]} */
export const notApplicable = ['error', 'loading'];

const settings = {
  renderHook: 'page:home:top',
  homepageOnly: false,
  titleTag: 'h2',
  titleColor: '#ffffff',
  subtitleColor: '#ffffff',
  captionStyle: 'glass',
  captionBackground: '#000000',
  captionOpacity: 0.5,
  blurAmount: 8,
  indicators: true,
  controls: true,
  transition: 'slide',
  autoSlide: false,
  interval: 5000,
  pauseOnHover: true,
  wrap: true,
};

/** @type {import('@panomc/plugin-kit').Samples} */
export default {
  filled: {
    props: {
      hookName: 'page:home:top',
      settings,
      sliderItems: [
        {
          imageUrl: 'https://example.com/slide-1.jpg',
          title: 'Welcome to the server',
          subtitle: 'Survival, minigames and a friendly community.',
          linkUrl: '/store',
          openInNewTab: false,
        },
        {
          imageUrl: 'https://example.com/slide-2.jpg',
          title: 'Summer event',
          subtitle: 'Join us every Saturday.',
          linkUrl: null,
          openInNewTab: false,
        },
      ],
    },
  },
  empty: {
    props: { hookName: 'page:home:top', settings, sliderItems: [] },
  },
};
