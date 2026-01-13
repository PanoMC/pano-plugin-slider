<script>
  import { _ } from "../../main";
  import { base } from "@panomc/sdk/svelte";
  import { Date } from "@panomc/sdk/components";
  import tooltip from "@panomc/sdk/utils/tooltip";

  export let slider;
  export let onEditClick;
  export let onDeleteClick;
  export let buttonsLoading = false;

  $: statusClass = slider.active ? 'bg-success' : 'bg-secondary';
  $: statusText = slider.active ? $_('pages.slider.table.status-active') : $_('pages.slider.table.status-inactive');
</script>

<td class="align-middle text-center p-0" style="width: 40px; cursor: grab;">
  <i class="fas fa-grip-vertical text-muted"></i>
</td>
<td class="align-middle clickable" on:click={() => onEditClick(slider.id)}>
  {#if slider.imageUrl}
    <div class="slider-preview">
      <img 
        src={slider.imageUrl.startsWith('http') ? slider.imageUrl : `${base}${slider.imageUrl}${slider.imageUrl.includes('?') ? '&' : '?'}thumbnail=true`} 
        alt={slider.title} 
        class="rounded" />
    </div>
  {:else}
    <div class="slider-preview-placeholder rounded bg-light d-flex align-middle justify-content-center align-items-center">
      <i class="fas fa-image text-muted"></i>
    </div>
  {/if}
</td>
<td class="align-middle clickable" on:click={() => onEditClick(slider.id)}>
  <div class="fw-bold">{slider.title}</div>
  {#if slider.subtitle}
    <div class="text-muted small">{slider.subtitle}</div>
  {/if}
</td>
<td class="align-middle text-center">
  <span class="badge bg-light text-dark border">{slider.itemOrder}</span>
</td>
<td class="align-middle text-center">
  <span class="badge {statusClass}">{statusText}</span>
</td>
<td class="align-middle">
  <div class="small text-muted">
    <Date time={slider.createdAt} />
  </div>
</td>
<td class="align-middle">
  <div class="small text-muted">
    <Date time={slider.updatedAt} />
  </div>
</td>
<td class="align-middle text-end text-nowrap">
  <button
    class="btn btn-sm btn-icon btn-light"
    aria-label={$_('buttons.edit')}
    use:tooltip={[$_('buttons.edit'), { placement: 'top' }]}
    on:click={() => onEditClick(slider.id)}
    disabled={buttonsLoading}>
    <i class="fas fa-edit"></i>
  </button>
  <button
    class="btn btn-sm btn-icon btn-light border-danger text-danger ms-1"
    aria-label={$_('buttons.delete')}
    use:tooltip={[$_('buttons.delete'), { placement: 'top' }]}
    on:click={() => onDeleteClick(slider.id)}
    disabled={buttonsLoading}>
    <i class="fas fa-trash"></i>
  </button>
</td>

<style>
  .slider-preview {
    width: 80px;
    height: 45px;
    overflow: hidden;
  }
  .slider-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .slider-preview-placeholder {
    width: 80px;
    height: 45px;
  }
  .btn-icon {
    width: 32px;
    height: 32px;
    padding: 0;
    line-height: 32px;
    text-align: center;
    border-radius: 8px;
  }
  .dragging {
    opacity: 0.4;
    background-color: var(--bs-light);
  }
  .clickable {
    cursor: pointer;
  }
</style>
