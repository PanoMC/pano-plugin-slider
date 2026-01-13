<article class="container vstack gap-3">
  <!-- Settings Card -->
  <div class="card mb-3">
    <CardHeader>
      <div slot="left" class="d-flex align-items-center">
        <i class="fas fa-cog me-2 text-primary"></i>
        <span class="fw-bold">
          {$_('pages.slider.settings.title')}
        </span>
      </div>
      <div slot="right">
        <button 
          class="btn btn-sm btn-success px-3" 
          on:click={onSaveSettings}
          disabled={savingSettings || !hasSettingsChanges}>
          {#if savingSettings}
            <i class="fas fa-spinner fa-spin me-1"></i>
          {:else}
            <i class="fas fa-save me-1"></i>
          {/if}
          {$_('buttons.save')}
        </button>
      </div>
    </CardHeader>
    <div class="card-body">
      <div class="row g-3">
        <!-- Render Hook -->
        <div class="col-md-6">
          <label class="form-label fw-semibold small text-uppercase text-muted">
            {$_('pages.slider.settings.fields.location')}
          </label>
          <select class="form-select" bind:value={settings.renderHook}>
            <option value="page:home:top">{$_('pages.slider.settings.locations.home-top')}</option>
            <option value="page:top">{$_('pages.slider.settings.locations.page-top')}</option>
          </select>
          <div class="form-text small">{$_('pages.slider.settings.fields.location-help')}</div>
        </div>

        <!-- Homepage Only -->
        <div class="col-md-6 d-flex align-items-center mt-md-4">
          <div class="form-check form-switch mt-2">
            <input 
              class="form-check-input" 
              type="checkbox" 
              id="homepageOnly" 
              bind:checked={settings.homepageOnly}
              disabled={settings.renderHook === 'page:home:top'}>
            <label class="form-check-label fw-semibold" for="homepageOnly">
              {$_('pages.slider.settings.fields.homepage-only')}
            </label>
            <div class="form-text small">{$_('pages.slider.settings.fields.homepage-only-help')}</div>
          </div>
        </div>

        <div class="col-12"><hr class="my-2 opacity-10"></div>

        <!-- Auto Slide & Interval -->
        <div class="col-md-4">
          <div class="form-check form-switch mb-2 pt-md-4">
            <input class="form-check-input" type="checkbox" id="autoSlide" bind:checked={settings.autoSlide}>
            <label class="form-check-label fw-semibold" for="autoSlide">{$_('pages.slider.settings.fields.auto-slide')}</label>
            <div class="form-text small">{$_('pages.slider.settings.fields.auto-slide-help')}</div>
          </div>
        </div>

        <div class="col-md-2">
          <label class="form-label fw-semibold small text-uppercase text-muted">{$_('pages.slider.settings.fields.interval')}</label>
          <input type="number" class="form-control" bind:value={settings.interval} min="1000" step="500" disabled={!settings.autoSlide}>
          <div class="form-text small">{$_('pages.slider.settings.fields.interval-help')}</div>
        </div>

        <div class="col-md-3">
          <div class="form-check form-switch mb-2 pt-md-4">
            <input class="form-check-input" type="checkbox" id="pauseOnHover" bind:checked={settings.pauseOnHover}>
            <label class="form-check-label fw-semibold" for="pauseOnHover">{$_('pages.slider.settings.fields.pause-on-hover')}</label>
            <div class="form-text small">{$_('pages.slider.settings.fields.pause-on-hover-help')}</div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="form-check form-switch mb-2 pt-md-4">
            <input class="form-check-input" type="checkbox" id="fade" bind:checked={settings.fade}>
            <label class="form-check-label fw-semibold" for="fade">{$_('pages.slider.settings.fields.fade')}</label>
            <div class="form-text small">{$_('pages.slider.settings.fields.fade-help')}</div>
          </div>
        </div>

        <!-- Indicators, Controls, Wrap -->
        <div class="col-md-4">
          <div class="form-check form-switch mb-2">
            <input class="form-check-input" type="checkbox" id="indicators" bind:checked={settings.indicators}>
            <label class="form-check-label fw-semibold" for="indicators">{$_('pages.slider.settings.fields.indicators')}</label>
            <div class="form-text small">{$_('pages.slider.settings.fields.indicators-help')}</div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="form-check form-switch mb-2">
            <input class="form-check-input" type="checkbox" id="controls" bind:checked={settings.controls}>
            <label class="form-check-label fw-semibold" for="controls">{$_('pages.slider.settings.fields.controls')}</label>
            <div class="form-text small">{$_('pages.slider.settings.fields.controls-help')}</div>
          </div>
        </div>

        <div class="col-md-4">
          <div class="form-check form-switch mb-2">
            <input class="form-check-input" type="checkbox" id="wrap" bind:checked={settings.wrap}>
            <label class="form-check-label fw-semibold" for="wrap">{$_('pages.slider.settings.fields.wrap')}</label>
            <div class="form-text small">{$_('pages.slider.settings.fields.wrap-help')}</div>
          </div>
        </div>

        <div class="col-12"><hr class="my-2 opacity-10"></div>

        <!-- Styling Settings -->
        <div class="col-md-3">
          <label class="form-label fw-semibold small text-uppercase text-muted">{$_('pages.slider.settings.fields.caption-style')}</label>
          <select class="form-select" bind:value={settings.captionStyle}>
            <option value="none">{$_('pages.slider.settings.fields.caption-style-none')}</option>
            <option value="solid">{$_('pages.slider.settings.fields.caption-style-solid')}</option>
            <option value="glass">{$_('pages.slider.settings.fields.caption-style-glass')}</option>
          </select>
        </div>

        <div class="col-md-3">
          <label class="form-label fw-semibold small text-uppercase text-muted">{$_('pages.slider.settings.fields.title-color')}</label>
          <div class="d-flex gap-2">
            <input type="color" class="form-control form-control-color" bind:value={settings.titleColor}>
            <input type="text" class="form-control" bind:value={settings.titleColor}>
          </div>
        </div>

        <div class="col-md-3">
          <label class="form-label fw-semibold small text-uppercase text-muted">{$_('pages.slider.settings.fields.subtitle-color')}</label>
          <div class="d-flex gap-2">
            <input type="color" class="form-control form-control-color" bind:value={settings.subtitleColor}>
            <input type="text" class="form-control" bind:value={settings.subtitleColor}>
          </div>
        </div>

        <div class="col-md-3">
          <label class="form-label fw-semibold small text-uppercase text-muted">{$_('pages.slider.settings.fields.caption-background')}</label>
          <div class="d-flex gap-2">
            <input type="color" class="form-control form-control-color" bind:value={settings.captionBackground}>
            <input type="text" class="form-control" bind:value={settings.captionBackground}>
          </div>
        </div>

        <div class="col-md-3">
          <label class="form-label fw-semibold small text-uppercase text-muted">{$_('pages.slider.settings.fields.caption-opacity')} ({settings.captionOpacity})</label>
          <input type="range" class="form-range" min="0" max="1" step="0.1" bind:value={settings.captionOpacity}>
        </div>

        {#if settings.captionStyle === 'glass'}
          <div class="col-md-3">
            <label class="form-label fw-semibold small text-uppercase text-muted">{$_('pages.slider.settings.fields.blur-amount')} ({settings.blurAmount}px)</label>
            <input type="range" class="form-range" min="0" max="40" step="1" bind:value={settings.blurAmount}>
          </div>
        {/if}
      </div>
    </div>
  </div>

  <!-- Action Menu -->
  <PageActions>
    <div slot="right">
      <button
        type="button"
        class="btn btn-primary"
        on:click={onCreateClick}>
        <i class="fas fa-plus me-2"></i>
        {$_('pages.slider.new-slider')}
      </button>
    </div>
  </PageActions>

  <!-- Slider Table -->
  <div class="card">
    <CardHeader>
      <div slot="left" class="d-flex align-items-center">
        <i class="fas fa-images me-2 text-primary"></i>
        <span class="fw-bold">
          {$_('pages.slider.count', { values: { count: data.sliderItems?.length || 0 } })}
        </span>
      </div>
      <CardFilters slot="right">
        <CardFiltersItem
          href="/view/slider"
          active={!currentStatus}>
          {$_('pages.slider.filters.all')}
        </CardFiltersItem>
        <CardFiltersItem
          href="/view/slider?status=ACTIVE"
          active={currentStatus === "ACTIVE"}>
          {$_('pages.slider.filters.active')}
        </CardFiltersItem>
        <CardFiltersItem
          href="/view/slider?status=INACTIVE"
          active={currentStatus === "INACTIVE"}>
          {$_('pages.slider.filters.inactive')}
        </CardFiltersItem>
      </CardFilters>
    </CardHeader>

    <!-- No Items -->
    {#if !data.sliderItems || data.sliderItems.length === 0}
      <div class="py-5">
        <NoContent />
      </div>
    {:else}
      <!-- Items Table -->
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="bg-light">
          <tr>
            <th scope="col" style="width: 40px;"></th>
            <th scope="col" style="width: 100px;">{$_('pages.slider.table.preview')}</th>
            <th scope="col">{$_('pages.slider.table.content')}</th>
            <th scope="col" class="text-center" style="width: 80px;">{$_('pages.slider.table.order')}</th>
            <th scope="col" class="text-center" style="width: 120px;">{$_('pages.slider.table.status')}</th>
            <th scope="col" style="width: 150px;">{$_('pages.slider.table.created-at')}</th>
            <th scope="col" style="width: 150px;">{$_('pages.slider.table.updated-at')}</th>
            <th scope="col" class="text-end" style="width: 120px;">{$_('pages.slider.table.actions')}</th>
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
              <SliderRow 
                {slider} 
                {buttonsLoading}
                onEditClick={onEditClick}
                onDeleteClick={onDeleteClick} />
            </tr>
          {/each}
          </tbody>
        </table>
      </div>
    {/if}
  </div>

  <!-- Modals -->
  <AddEditSliderModal />
  <ConfirmDeleteSliderModal />
</article>

<script context="module">
  import ApiUtil, { buildQueryParams } from "@panomc/sdk/utils/api";

  /**
   * @type {import("@sveltejs/kit").PageLoad}
   */
  export async function load(event) {
    const { parent, url: { searchParams } } = event;
    const { pageTitle } = await parent();

    pageTitle.set('plugins.pano-plugin-slider.pages.slider.title');

    const statusParam = searchParams.get("status");
    let sliderItems = [];
    let settings = { renderHook: "page:home:top", homepageOnly: true };

    await ApiUtil.get({
      path: "/api/panel/slider/items",
      handler: (body) => {
        if (body.result === 'ok') {
          sliderItems = body.sliderItems;
        }
      }
    });

    await ApiUtil.get({
      path: "/api/panel/slider/settings",
      handler: (body) => {
        if (body.result === 'ok') {
          settings = body.settings;
        }
      }
    });

    if (statusParam === "ACTIVE") {
      sliderItems = sliderItems.filter(i => i.active);
    } else if (statusParam === "INACTIVE") {
      sliderItems = sliderItems.filter(i => !i.active);
    }

    return { data: { sliderItems, settings } };
  }
</script>

<script>
  import { base, page, goto } from "@panomc/sdk/svelte";
  import { flip } from "svelte/animate";
  import { showToast } from "@panomc/sdk/toasts";
  import {
    PageActions,
    CardHeader,
    CardFilters,
    CardFiltersItem,
    NoContent
  } from "@panomc/sdk/components";

  import { _ } from "../main";
  import SliderRow from "./components/SliderRow.svelte";
  import ConfirmDeleteSliderModal, {
    show as showDeleteModal,
    setCallback as setDeleteCallback
  } from "./components/modals/ConfirmDeleteSliderModal.svelte";
  import AddEditSliderModal, {
    show as showAddEditModal,
    setCallback as setAddEditModalCallback
  } from "./components/modals/AddEditSliderModal.svelte";

  export let data;

  $: currentStatus = $page.url.searchParams.get("status");
  let draggedId = null;
  let initialSettingsStr = "";
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
        path: "/api/panel/slider/settings",
        body: settings
      });

      if (result.result === 'ok') {
        showToast($_('toasts.settings-save-success'));
        initialSettingsStr = JSON.stringify(settings);
      } else {
        showToast(result.error || 'Error saving settings');
      }
    } catch (e) {
      console.error(e);
      showToast('Error saving settings');
    } finally {
      savingSettings = false;
    }
  }

  async function refreshData() {
    const statusVal = $page.url.searchParams.get("status");
    const queryParams = buildQueryParams({
      status: statusVal
    });
    await goto(`${base}/view/slider${queryParams}`, { invalidateAll: true });
  }

  function onDragStart(e, id) {
    draggedId = id;
    e.dataTransfer.effectAllowed = "move";
    const img = new Image();
    img.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7';
    e.dataTransfer.setDragImage(img, 0, 0);
  }

  function onDragEnter(e, targetId) {
    if (!draggedId || draggedId === targetId) return;

    const items = [...data.sliderItems];
    const fromIndex = items.findIndex(i => i.id === draggedId);
    const targetIndex = items.findIndex(i => i.id === targetId);

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

    const ids = data.sliderItems.map(item => item.id);
    const res = await ApiUtil.post({
      path: "/api/panel/slider/items/reorder",
      body: { ids }
    });

    if (res.result === 'ok') {
      showToast($_('toasts.reorder-success'));
      await refreshData();
    }
  }

  function onCreateClick() {
    showAddEditModal("create");
  }

  function onEditClick(id) {
    const slider = data.sliderItems.find((s) => s.id === id);
    if (slider) {
      showAddEditModal("edit", slider);
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

<style>
  .card {
    border-radius: 12px;
  }
  .table thead th {
    font-size: 0.75rem;
    text-transform: uppercase;
    letter-spacing: 0.025em;
    font-weight: 700;
    color: #6c757d;
    border-top: none;
  }
  tr {
    transition: transform 0.3s ease, background-color 0.2s ease;
  }
  .dragging {
    opacity: 0.4;
    background-color: var(--bs-body-tertiary);
  }
</style>
