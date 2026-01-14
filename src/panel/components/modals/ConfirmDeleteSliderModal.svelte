<div class="modal fade" bind:this={$modalElement} role="dialog" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered" role="dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          {$_('components.modals.confirm-delete-slider.title')}
        </h5>
        <button
          title={$_('buttons.close')}
          aria-label={$_('buttons.close')}
          type="button"
          class="btn-close"
          data-bs-dismiss="modal"
          on:click={hide}></button>
      </div>
      <div class="modal-body text-center">
        <div class="mb-3">
          <i class="fas fa-question-circle fa-3x"></i>
        </div>
        {@html $_('components.modals.confirm-delete-slider.description', { values: { title: `<strong>${$slider.title}</strong>` } })}
      </div>
      <div class="modal-footer flex-nowrap">
        <button
          class="btn btn-link col-6"
          type="button"
          on:click={hide}
          disabled={loading}>
          {$_('buttons.cancel')}
        </button>
        <button
          class="btn btn-danger col-6"
          type="button"
          on:click={onConfirm}
          disabled={loading}>
          {#if loading}
            <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
          {/if}
          {$_('buttons.delete')}
        </button>
      </div>
    </div>
  </div>
</div>

<script context="module">
  import { writable, get } from 'svelte/store';

  const modalElement = writable();
  const slider = writable({});
  const loadingStore = writable(false);

  let callback = () => {};
  let modal;

  export function show(newSlider) {
    slider.set(newSlider);
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
  import { showToast } from "@panomc/sdk/toasts";

  $: loading = $loadingStore;

  async function onConfirm() {
    $loadingStore = true;

    try {
      const result = await ApiUtil.delete({
        path: `/api/panel/slider/items/${$slider.id}`
      });

      if (result.result === 'ok') {
        showToast($_('toasts.item-deleted'));
        hide();
        callback();
      } else {
        showToast($_('toasts.delete-failed'), { values: { error: result.error } });
      }
    } catch (e) {
      console.error(e);
      showToast($_('toasts.delete-failed'), { values: { error: e.message } });
    } finally {
      $loadingStore = false;
    }
  }
</script>
