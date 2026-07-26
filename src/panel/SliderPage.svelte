<style>
  tr {
    transition:
      transform 0.3s ease,
      background-color 0.2s ease;
  }

  .dragging {
    opacity: 0.4;
    background-color: var(--bs-body-tertiary);
  }
</style>

<article class="container vstack gap-3">
  <!-- Action Menu -->
  <PageActions leftClasses="d-lg-flex d-none" middleClasses="d-lg-flex d-none">
    <div slot="right">
      <button type="button" class="btn btn-secondary" on:click={onCreateClick}>
        <i class="fas fa-plus"></i>
        <span class="d-lg-inline d-none">
          {$_('pages.slider.add-slider')}
        </span>
      </button>
    </div>
  </PageActions>

  <!-- Slider Table -->
  <div class="card">
    <CardHeader>
      <div slot="left">
        {$_('pages.slider.count', {
          values: { count: data.sliderItems?.length || 0 },
        })}
      </div>
      <CardFilters slot="right">
        <CardFiltersItem href="/view/slider" active={!currentStatus}>
          {$_('pages.slider.filters.all')}
        </CardFiltersItem>
        <CardFiltersItem href="/view/slider?status=ACTIVE" active={currentStatus === 'ACTIVE'}>
          {$_('pages.slider.filters.active')}
        </CardFiltersItem>
        <CardFiltersItem href="/view/slider?status=INACTIVE" active={currentStatus === 'INACTIVE'}>
          {$_('pages.slider.filters.inactive')}
        </CardFiltersItem>
      </CardFilters>
    </CardHeader>

    <!-- No Items -->
    {#if !data.sliderItems || data.sliderItems.length === 0}
      <NoContent />
    {:else}
      <!-- Items Table -->
      <div class="table-responsive">
        <table class="table table-hover align-middle">
          <thead>
            <tr>
              <th scope="col" style="width: 50px;"></th>
              <th scope="col" style="width: 40px;"></th>
              <th scope="col" style="width: 100px;"></th>
              <th scope="col">{$_('pages.slider.table.content')}</th>
              <th scope="col" class="text-center" style="width: 80px;"
                >{$_('pages.slider.table.order')}</th>
              <th scope="col" class="text-center" style="width: 120px;"
                >{$_('pages.slider.table.status')}</th>
              <th scope="col" style="width: 150px;">{$_('pages.slider.table.created-at')}</th>
              <th scope="col" style="width: 150px;">{$_('pages.slider.table.updated-at')}</th>
            </tr>
          </thead>
          <tbody on:dragover|preventDefault on:drop={onDrop}>
            {#each data.sliderItems as slider (slider.id)}
              <tr
                animate:flip={{ duration: 300 }}
                draggable="true"
                on:dragstart={(e) => onDragStart(e, slider.id)}
                on:dragover|preventDefault
                on:dragenter={(e) => onDragEnter(e, slider.id)}
                on:dragend={onDragEnd}
                class:dragging={slider.id === draggedId}>
                <SliderRow {slider} {buttonsLoading} {onEditClick} {onDeleteClick} />
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    {/if}
  </div>
  <!-- Settings Card -->
  <div class="card">
    <div class="card-header">
      {$_('pages.slider.settings.title')}
    </div>
    <div class="card-body">
      <!-- Location -->
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="renderHook">
          {$_('pages.slider.settings.fields.location')}
        </label>
        <div class="col-md-6">
          <select class="form-control" id="renderHook" bind:value={settings.renderHook}>
            <option value="page:home:top">{$_('pages.slider.settings.locations.home-top')}</option>
            <option value="page:top">{$_('pages.slider.settings.locations.page-top')}</option>
          </select>
        </div>
      </div>

      <!-- Homepage Only -->
      <div class="row mb-3">
        <label class="col-md-6" for="homepageOnly">
          {$_('pages.slider.settings.fields.homepage-only')}
        </label>
        <div class="col-md-6 d-flex align-items-center">
          <div class="form-check form-switch">
            <input
              class="form-check-input"
              type="checkbox"
              id="homepageOnly"
              bind:checked={settings.homepageOnly}
              disabled={settings.renderHook === 'page:home:top'} />
          </div>
        </div>
      </div>

      <!-- Auto Slide -->
      <div class="row mb-3">
        <label class="col-md-6" for="autoSlide">
          {$_('pages.slider.settings.fields.auto-slide')}
          <small class="d-block">{$_('pages.slider.settings.fields.auto-slide-help')}</small>
        </label>
        <div class="col-md-6 d-flex align-items-center">
          <div class="form-check form-switch">
            <input
              class="form-check-input"
              type="checkbox"
              id="autoSlide"
              bind:checked={settings.autoSlide} />
          </div>
        </div>
      </div>

      <!-- Interval -->
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="interval">
          {$_('pages.slider.settings.fields.interval')}
          <small class="d-block">{$_('pages.slider.settings.fields.interval-help')}</small>
        </label>
        <div class="col-md-6">
          <input
            type="number"
            class="form-control"
            id="interval"
            bind:value={settings.interval}
            min="1000"
            step="500"
            disabled={!settings.autoSlide} />
        </div>
      </div>

      <!-- Pause On Hover -->
      <div class="row mb-3">
        <label class="col-md-6" for="pauseOnHover">
          {$_('pages.slider.settings.fields.pause-on-hover')}
          <small class="d-block">
            {$_('pages.slider.settings.fields.pause-on-hover-help')}
          </small>
        </label>
        <div class="col-md-6 d-flex align-items-center">
          <div class="form-check form-switch">
            <input
              class="form-check-input"
              type="checkbox"
              id="pauseOnHover"
              bind:checked={settings.pauseOnHover} />
          </div>
        </div>
      </div>

      <!-- Transition -->
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="transition">
          {$_('pages.slider.settings.fields.transition')}
          <small class="d-block">{$_('pages.slider.settings.fields.transition-help')}</small>
        </label>
        <div class="col-md-6">
          <select class="form-select" id="transition" bind:value={settings.transition}>
            <option value="slide">{$_('pages.slider.settings.fields.transition-slide')}</option>
            <option value="fade">{$_('pages.slider.settings.fields.transition-fade')}</option>
          </select>
        </div>
      </div>

      <!-- Indicators -->
      <div class="row mb-3">
        <label class="col-md-6" for="indicators">
          {$_('pages.slider.settings.fields.indicators')}
          <small class="d-block">{$_('pages.slider.settings.fields.indicators-help')}</small>
        </label>
        <div class="col-md-6 d-flex align-items-center">
          <div class="form-check form-switch">
            <input
              class="form-check-input"
              type="checkbox"
              id="indicators"
              bind:checked={settings.indicators} />
          </div>
        </div>
      </div>

      <!-- Controls -->
      <div class="row mb-3">
        <label class="col-md-6" for="controls">
          {$_('pages.slider.settings.fields.controls')}
          <small class="d-block">{$_('pages.slider.settings.fields.controls-help')}</small>
        </label>
        <div class="col-md-6 d-flex align-items-center">
          <div class="form-check form-switch">
            <input
              class="form-check-input"
              type="checkbox"
              id="controls"
              bind:checked={settings.controls} />
          </div>
        </div>
      </div>

      <!-- Wrap -->
      <div class="row mb-3">
        <label class="col-md-6" for="wrap">
          {$_('pages.slider.settings.fields.wrap')}
          <small class="d-block">{$_('pages.slider.settings.fields.wrap-help')}</small>
        </label>
        <div class="col-md-6 d-flex align-items-center">
          <div class="form-check form-switch">
            <input
              class="form-check-input"
              type="checkbox"
              id="wrap"
              bind:checked={settings.wrap} />
          </div>
        </div>
      </div>

      <!-- Title Tag -->
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="titleTag">
          {$_('pages.slider.settings.fields.title-tag')}
        </label>
        <div class="col-md-6">
          <div class="btn-group w-100" role="group">
            {#each ['h1', 'h2', 'h3', 'h4', 'h5', 'h6'] as tag}
              <input
                type="radio"
                class="btn-check"
                name="titleTag"
                id="tag-{tag}"
                value={tag}
                bind:group={settings.titleTag} />
              <label class="btn btn-outline-primary btn-sm" for="tag-{tag}">
                {tag.toUpperCase()}
              </label>
            {/each}
          </div>
        </div>
      </div>
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="captionStyle">
          {$_('pages.slider.settings.fields.caption-style')}
        </label>
        <div class="col-md-6">
          <select class="form-select" id="captionStyle" bind:value={settings.captionStyle}>
            <option value="none">{$_('pages.slider.settings.fields.caption-style-none')}</option>
            <option value="solid">{$_('pages.slider.settings.fields.caption-style-solid')}</option>
            <option value="glass">{$_('pages.slider.settings.fields.caption-style-glass')}</option>
            <option value="gradient">{$_('pages.slider.settings.fields.caption-style-gradient')}</option>
          </select>
        </div>
      </div>

      <!-- Title Color -->
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="titleColor">
          {$_('pages.slider.settings.fields.title-color')}
        </label>
        <div class="col-md-6">
          <div class="d-flex gap-2">
            <input
              type="color"
              class="form-control form-control-color"
              id="titleColor"
              bind:value={settings.titleColor} />
            <input type="text" class="form-control" bind:value={settings.titleColor} />
          </div>
        </div>
      </div>

      <!-- Subtitle Color -->
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="subtitleColor">
          {$_('pages.slider.settings.fields.subtitle-color')}
        </label>
        <div class="col-md-6">
          <div class="d-flex gap-2">
            <input
              type="color"
              class="form-control form-control-color"
              id="subtitleColor"
              bind:value={settings.subtitleColor} />
            <input type="text" class="form-control" bind:value={settings.subtitleColor} />
          </div>
        </div>
      </div>

      <!-- Caption Background -->
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="captionBackground">
          {$_('pages.slider.settings.fields.caption-background')}
        </label>
        <div class="col-md-6">
          <div class="d-flex gap-2">
            <input
              type="color"
              class="form-control form-control-color"
              id="captionBackground"
              bind:value={settings.captionBackground} />
            <input type="text" class="form-control" bind:value={settings.captionBackground} />
          </div>
        </div>
      </div>

      <!-- Caption Opacity -->
      <div class="row mb-3">
        <label class="col-md-6 col-form-label" for="captionOpacity">
          {$_('pages.slider.settings.fields.caption-opacity')} ({settings.captionOpacity})
        </label>
        <div class="col-md-6 d-flex align-items-center">
          <input
            type="range"
            class="form-range"
            id="captionOpacity"
            min="0"
            max="1"
            step="0.1"
            bind:value={settings.captionOpacity} />
        </div>
      </div>

      {#if settings.captionStyle === 'glass'}
        <div class="row mb-3">
          <label class="col-md-6 col-form-label" for="blurAmount">
            {$_('pages.slider.settings.fields.blur-amount')} ({settings.blurAmount}px)
          </label>
          <div class="col-md-6 d-flex align-items-center">
            <input
              type="range"
              class="form-range"
              id="blurAmount"
              min="0"
              max="40"
              step="1"
              bind:value={settings.blurAmount} />
          </div>
        </div>
      {/if}

      <button
        class="btn btn-secondary"
        on:click={onSaveSettings}
        disabled={savingSettings || !hasSettingsChanges}>
        {$_('buttons.save')}
      </button>
    </div>
  </div>

  <!-- Modals -->
  <AddEditSliderModal />
  <ConfirmDeleteSliderModal />
</article>

<script context="module">
  import ApiUtil, {buildQueryParams} from '@panomc/sdk/utils/api';

  /**
   * @type {import("@sveltejs/kit").PageLoad}
   */
  export async function load(event) {
    const {
      parent,
      url: { searchParams },
    } = event;
    const { pageTitle } = await parent();

    pageTitle.set('plugins.pano-plugin-slider.pages.slider.title');

    const statusParam = searchParams.get('status');
    let sliderItems = [];
    let settings = { renderHook: 'page:home:top', homepageOnly: true };

    await ApiUtil.get({
      path: '/api/panel/slider/items',
      handler: (body) => {
        if (body.result === 'ok') {
          sliderItems = body.sliderItems;
        }
      },
    });

    await ApiUtil.get({
      path: '/api/panel/slider/settings',
      handler: (body) => {
        if (body.result === 'ok') {
          settings = body.settings;
        }
      },
    });

    if (statusParam === 'ACTIVE') {
      sliderItems = sliderItems.filter((i) => i.active);
    } else if (statusParam === 'INACTIVE') {
      sliderItems = sliderItems.filter((i) => !i.active);
    }

    return { data: { sliderItems, settings } };
  }
</script>

<script>
  import { base, page, goto } from '@panomc/sdk/svelte';
  import { flip } from 'svelte/animate';
  import {
    PageActions,
    CardHeader,
    CardFilters,
    CardFiltersItem,
    NoContent,
  } from '@panomc/sdk/components/panel';

  import { _, showSuccessToast, showErrorToast } from '../main';
  import SliderRow from './components/SliderRow.svelte';
  import ConfirmDeleteSliderModal, {
    show as showDeleteModal,
    setCallback as setDeleteCallback,
  } from './components/modals/ConfirmDeleteSliderModal.svelte';
  import AddEditSliderModal, {
    show as showAddEditModal,
    setCallback as setAddEditModalCallback,
  } from './components/modals/AddEditSliderModal.svelte';

  export let data;

  $: currentStatus = $page.url.searchParams.get('status');
  let draggedId = null;
  let initialSettingsStr = '';
  let buttonsLoading = false;
  let savingSettings = false;

  $: settings = data.settings;

  $: if (settings && !initialSettingsStr) {
    initialSettingsStr = JSON.stringify(settings);
  }

  $: hasSettingsChanges = initialSettingsStr !== JSON.stringify(settings);

  $: if (settings.renderHook === 'page:home:top') {
    settings.homepageOnly = true;
  }

  async function onSaveSettings() {
    savingSettings = true;
    try {
      const result = await ApiUtil.post({
        path: '/api/panel/slider/settings',
        body: settings,
      });

      if (result.result === 'ok') {
        showSuccessToast($_('toasts.settings-save-success'));
        initialSettingsStr = JSON.stringify(settings);
      } else {
        showErrorToast(result.error || 'Error saving settings');
      }
    } catch (e) {
      console.error(e);
      showErrorToast('Error saving settings');
    } finally {
      savingSettings = false;
    }
  }

  async function refreshData() {
    const statusVal = $page.url.searchParams.get('status');
    const queryParams = buildQueryParams({
      status: statusVal,
    });
    await goto(`${base}/view/slider${queryParams}`, { invalidateAll: true });
  }

  function onDragStart(e, id) {
    draggedId = id;
    e.dataTransfer.effectAllowed = 'move';
    const img = new Image();
    img.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
    e.dataTransfer.setDragImage(img, 0, 0);
  }

  function onDragEnter(e, targetId) {
    if (!draggedId || draggedId === targetId) return;

    const items = [...data.sliderItems];
    const fromIndex = items.findIndex((i) => i.id === draggedId);
    const targetIndex = items.findIndex((i) => i.id === targetId);

    if (fromIndex !== -1 && targetIndex !== -1) {
      const [movedItem] = items.splice(fromIndex, 1);
      items.splice(targetIndex, 0, movedItem);

      items.forEach((item, index) => {
        item.itemOrder = index + 1;
      });

      data.sliderItems = items;
    }
  }

  function onDragEnd() {
    draggedId = null;
  }

  async function onDrop(e) {
    e.preventDefault();
    draggedId = null;

    const ids = data.sliderItems.map((item) => item.id);
    const res = await ApiUtil.post({
      path: '/api/panel/slider/items/reorder',
      body: { ids },
    });

    if (res.result === 'ok') {
      showSuccessToast($_('toasts.reorder-success'));
      await refreshData();
    }
  }

  function onCreateClick() {
    showAddEditModal('create');
  }

  function onEditClick(id) {
    const slider = data.sliderItems.find((s) => s.id === id);
    if (slider) {
      showAddEditModal('edit', slider);
    }
  }

  function onDeleteClick(id) {
    const slider = data.sliderItems.find((s) => s.id === id);
    if (slider) {
      showDeleteModal(slider);
    }
  }

  setAddEditModalCallback(() => {
    refreshData();
  });

  setDeleteCallback(() => {
    refreshData();
  });
</script>
