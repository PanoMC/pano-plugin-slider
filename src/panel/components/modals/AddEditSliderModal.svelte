<div class="modal fade" bind:this={$modalElement} role="dialog" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered modal-lg" role="dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          {$mode === 'edit'
            ? $_('components.modals.add-edit-slider.edit-title')
            : $_('components.modals.add-edit-slider.add-title')}
        </h5>
        <button
          title={$_('buttons.close')}
          aria-label={$_('buttons.close')}
          type="button"
          class="btn-close"
          data-bs-dismiss="modal"
          on:click={hide}></button>
      </div>
      <form on:submit|preventDefault={onSubmit}>
        <div class="modal-body">
          <div class="row g-3">
            <!-- Preview Section -->
            <div class="col-12 text-center">
              <div 
                class="preview-container rounded border d-flex align-items-center justify-content-center bg-body-tertiary overflow-hidden position-relative" 
                class:drag-over={dropZoneActive}
                style="height: 220px; cursor: pointer;"
                on:click={() => fileInput.click()}
                on:dragover={handleDragOver}
                on:dragleave={handleDragLeave}
                on:drop={handleDrop}>
                {#if displayImageUrl}
                  <img src={displayImageUrl} alt="Preview" class="w-100 h-100 object-fit-cover" on:error={onImageError} />
                  <div class="preview-overlay position-absolute bottom-0 start-0 w-100 p-3 text-white text-start">
                    <div class="fw-bold">{$slider.title || $_('components.modals.add-edit-slider.placeholders.title')}</div>
                    <div class="small opacity-75">{$slider.subtitle || $_('components.modals.add-edit-slider.placeholders.subtitle')}</div>
                  </div>
                  <div class="position-absolute top-0 end-0 p-2">
                    <button type="button" class="btn btn-sm btn-dark bg-opacity-50 border-0 rounded-circle" on:click|stopPropagation={onRemoveImage}>
                      <i class="fas fa-times"></i>
                    </button>
                  </div>
                {:else}
                  <div class="text-center text-muted">
                    <i class="fas fa-image fa-3x mb-2 opacity-50"></i>
                    <div class="fw-bold">{$_('components.modals.add-edit-slider.no-preview')}</div>
                    <div class="small opacity-75 mt-1">{$_('components.modals.add-edit-slider.image-drop-placeholder')}</div>
                  </div>
                {/if}
              </div>
            </div>

            <!-- Title & Subtitle -->
            <div class="col-md-6">
              <label for="slider-title" class="form-label small fw-bold">{$_('components.modals.add-edit-slider.fields.title')}</label>
              <input
                id="slider-title"
                type="text"
                class="form-control"
                placeholder={$_('components.modals.add-edit-slider.placeholders.title')}
                bind:value={$slider.title} />
            </div>
            <div class="col-md-6">
              <label for="slider-subtitle" class="form-label small fw-bold">{$_('components.modals.add-edit-slider.fields.subtitle')}</label>
              <input
                id="slider-subtitle"
                type="text"
                class="form-control"
                placeholder={$_('components.modals.add-edit-slider.placeholders.subtitle')}
                bind:value={$slider.subtitle} />
            </div>

            <!-- Hidden File Input -->
            <input
              id="slider-image"
              type="file"
              class="d-none"
              accept="image/png,image/jpeg,image/gif,image/webp"
              on:change={onFileChange}
              bind:this={fileInput} />

            <!-- Link URL -->
            <div class="col-md-8">
              <label for="slider-link" class="form-label small fw-bold">{$_('components.modals.add-edit-slider.fields.link-url')}</label>
              <div class="input-group">
                <span class="input-group-text"><i class="fas fa-external-link-alt"></i></span>
                <input
                  id="slider-link"
                  type="text"
                  class="form-control"
                  placeholder="/category/featured"
                  bind:value={$slider.linkUrl} />
              </div>
            </div>

            <!-- Order -->
            {#if $mode === 'edit'}
              <div class="col-md-4">
                <label for="slider-order" class="form-label small fw-bold">{$_('components.modals.add-edit-slider.fields.order')}</label>
                <input
                  id="slider-order"
                  type="number"
                  class="form-control"
                  bind:value={$slider.itemOrder}
                  min="1" />
              </div>
            {/if}

            <!-- Status Switch -->
            <div class="col-12">
              <div class="row g-2">
                <div class="col-md-6">
                  <div class="form-check form-switch h-100">
                    <input
                      class="form-check-input ms-0 me-2"
                      type="checkbox"
                      role="switch"
                      id="slider-status"
                      bind:checked={$slider.active} />
                    <label class="form-check-label fw-bold" for="slider-status">
                      {$slider.active ? $_('pages.slider.table.status-active') : $_('pages.slider.table.status-inactive')}
                    </label>
                    <div class="small text-muted ms-4">{$_('components.modals.add-edit-slider.fields.status-help')}</div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="form-check form-switch h-100">
                    <input
                      class="form-check-input ms-0 me-2"
                      type="checkbox"
                      role="switch"
                      id="slider-new-tab"
                      bind:checked={$slider.openInNewTab} />
                    <label class="form-check-label fw-bold" for="slider-new-tab">
                      {$_('components.modals.add-edit-slider.fields.open-in-new-tab')}
                    </label>
                    <div class="small text-muted ms-4">{$_('components.modals.add-edit-slider.placeholders.new-tab-help') || 'Open this link in a new browser tab?'}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer border-top-0">
          <button class="btn btn-secondary px-4" type="button" on:click={hide} disabled={loading}>
            {$_('buttons.cancel')}
          </button>
          <button class="btn btn-primary px-5" type="submit" disabled={loading || !canSave}>
            {#if loading}
              <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
            {/if}
            {$mode === 'edit' ? $_('buttons.save') : $_('buttons.create')}
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<script context="module">
  import { writable, get } from 'svelte/store';

  const modalElement = writable();
  const mode = writable('create');
  const slider = writable({
    title: '',
    subtitle: '',
    imageUrl: '',
    linkUrl: '',
    openInNewTab: false,
    itemOrder: 1,
    active: true
  });
  const loadingStore = writable(false);
  const selectedFile = writable(null);
  const previewUrl = writable(null);
  const initialData = writable(null);

  let callback = () => {};
  let modal;

  export function show(newMode, data = null) {
    mode.set(newMode);
    selectedFile.set(null);
    previewUrl.set(null);
    if (data) {
      const copy = { ...data };
      slider.set(copy);
      initialData.set({ ...copy });
    } else {
      const reset = {
        title: '',
        subtitle: '',
        imageUrl: '',
        linkUrl: '',
        openInNewTab: false,
        itemOrder: 1,
        active: true
      };
      slider.set(reset);
      initialData.set({ ...reset });
    }
    loadingStore.set(false);

    modal = new window.bootstrap.Modal(get(modalElement), {
      backdrop: 'static',
      keyboard: false,
    });
    modal.show();
  }

  export function hide() {
    modal.hide();
  }

  export function setCallback(newCallback) {
    callback = newCallback;
  }
</script>

<script>
  import { _ } from "../../../main";
  import ApiUtil from "@panomc/sdk/utils/api";
  import { base } from "@panomc/sdk/svelte";
  import { showToast } from "@panomc/sdk/toasts";

  let fileInput;
  let dropZoneActive = false;

  $: loading = $loadingStore;
  $: hasDataChanges = $initialData && (
    $slider.title !== $initialData.title ||
    $slider.subtitle !== $initialData.subtitle ||
    $slider.linkUrl !== $initialData.linkUrl ||
    $slider.openInNewTab !== $initialData.openInNewTab ||
    $slider.itemOrder !== $initialData.itemOrder ||
    $slider.active !== $initialData.active ||
    $slider.imageUrl !== $initialData.imageUrl
  );
  $: isFormValid = $selectedFile || ($mode === 'create' ? false : ($slider.imageUrl && hasDataChanges)) || ($mode === 'create' ? ($slider.imageUrl) : false);
  
  // Refined validation: 
  // In 'create' mode: must have a file OR an imageUrl (if somehow set).
  // In 'edit' mode: must have a selectedFile OR (imageUrl exists AND (data changed OR file removed/changed)).
  $: canSave = ($mode === 'create') 
    ? ($selectedFile || $slider.imageUrl)
    : ($selectedFile || hasDataChanges);

  $: displayImageUrl = $previewUrl || ($slider.imageUrl ? ( $slider.imageUrl.startsWith('http') ? $slider.imageUrl : `${base}${$slider.imageUrl}` ) : null);

  function handleDragOver(e) {
    e.preventDefault();
    dropZoneActive = true;
  }

  function handleDragLeave() {
    dropZoneActive = false;
  }

  function handleDrop(e) {
    e.preventDefault();
    dropZoneActive = false;
    const file = e.dataTransfer.files[0];
    if (file) {
      processFile(file);
    }
  }

  function onFileChange(event) {
    const file = event.target.files[0];
    if (file) {
      processFile(file);
    }
  }

  function processFile(file) {
    const maxSize = 5 * 1024 * 1024;
    const allowedTypes = ["image/png", "image/jpeg", "image/gif", "image/webp"];

    if (file.size > maxSize) {
      showToast($_('toasts.image-size-error'));
      if (fileInput) fileInput.value = "";
      return;
    }

    if (!allowedTypes.includes(file.type)) {
      showToast('Invalid file type');
      if (fileInput) fileInput.value = "";
      return;
    }

    selectedFile.set(file);
    const reader = new FileReader();
    reader.onload = (e) => {
      previewUrl.set(e.target.result);
    };
    reader.readAsDataURL(file);
  }

  function onRemoveImage() {
    selectedFile.set(null);
    previewUrl.set(null);
    $slider.imageUrl = '';
    if (fileInput) fileInput.value = "";
  }

  function onImageError() {
    // Optionally handle image load error
  }

  async function onSubmit() {
    $loadingStore = true;
    
    try {
      const isEdit = $mode === 'edit';
      const path = isEdit ? `/api/panel/slider/items/${$slider.id}` : '/api/panel/slider/items';

      const formData = new FormData();
      formData.append('title', $slider.title);
      formData.append('subtitle', $slider.subtitle || '');
      formData.append('linkUrl', $slider.linkUrl || '');
      formData.append('openInNewTab', $slider.openInNewTab);
      formData.append('itemOrder', $slider.itemOrder || 0);
      formData.append('active', $slider.active);

      if ($selectedFile) {
        formData.append('image', $selectedFile);
      }

      const result = await ApiUtil[isEdit ? 'put' : 'post']({
        path,
        body: formData,
        headers: {} // FormData automatically sets correct multi-part headers
      });

      if (result.result === 'ok') {
        showToast(isEdit ? $_('toasts.item-updated') : $_('toasts.item-created'));
        hide();
        callback();
      } else {
        showToast($_('toasts.save-failed'), { values: { error: result.error } });
      }
    } catch (e) {
      console.error(e);
      showToast($_('toasts.save-failed'), { values: { error: e.message } });
    } finally {
      $loadingStore = false;
    }
  }
</script>

<style>
  .preview-container {
    transition: all 0.2s ease;
  }

  .form-check-input {
    cursor: pointer;
  }

  .preview-overlay {
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
  }

  .drag-over {
    border-color: var(--bs-primary) !important;
    background-color: var(--bs-primary-bg-subtle) !important;
    border-width: 2px !important;
    border-style: dashed !important;
  }
</style>
